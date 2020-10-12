package com.baidu.shop.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.feign.CategoryFeign;
import com.baidu.feign.GoodsFeign;
import com.baidu.response.GoodsResponse;
import com.baidu.base.BaseApiService;
import com.baidu.base.Result;
import com.baidu.document.GoodsDoc;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.entity.SpuDetailEntity;
import com.baidu.feign.BrandFeign;
import com.baidu.feign.SpecificationFeign;
import com.baidu.shop.service.ShopElasticsearchService;
import com.baidu.status.HTTPStatus;
import com.baidu.utils.ESHighLightUtil;
import com.baidu.utils.JSONUtil;
import com.baidu.utils.StringUtil;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName ShopElasticsearchServiceImpl
 * @Description: TODO
 * @Author gengzifang
 * @Date 2020/9/16
 * @Version V1.0
 **/
@RestController
public class ShopElasticsearchServiceImpl extends BaseApiService implements ShopElasticsearchService {

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private SpecificationFeign specificationFeign;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private BrandFeign brandFeign;

    @Autowired
    private CategoryFeign categoryFeign;


    @Override
    public Result<JSONObject> saveData(Integer spuId) {

        //通过SpuId查询数据
        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setId(spuId);

        //这行代码有可能查询不到数据的
        List<GoodsDoc> goodsDocs = this.esGoodsInfo(spuDTO);
        GoodsDoc goodsDoc = goodsDocs.get(0);
        elasticsearchRestTemplate.save(goodsDoc);

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> delData(Integer spuId) {

        GoodsDoc goodsDoc = new GoodsDoc();
        goodsDoc.setId(spuId.longValue());
        elasticsearchRestTemplate.delete(goodsDoc);

        return this.setResultSuccess();
    }

    @Override
    public GoodsResponse search(String search, Integer page, String filter) {

        //判断搜索条件不为null
        if (StringUtil.isEmpty(search)) throw new RuntimeException("搜索条件不能不能为空");

        //查询
        SearchHits<GoodsDoc> hits = elasticsearchRestTemplate.search(this.getSearchQuery(search, page,filter).build(), GoodsDoc.class);

        //将字段设置为高亮字段
        List<SearchHit<GoodsDoc>> highLightHit = ESHighLightUtil.getHighLightHit(hits.getSearchHits());

        //返回的商品集合
        List<GoodsDoc> goodsDocList = highLightHit.stream().map(searchHit -> searchHit.getContent()).collect(Collectors.toList());

        //分页总条数/总页数
        Integer total = Long.valueOf(hits.getTotalHits()).intValue();
        Integer totalPage = Double.valueOf(Math.ceil(Long.valueOf(hits.getTotalHits()).doubleValue() / 10)).intValue();

        //获取聚合数据
        Aggregations aggregations = hits.getAggregations();

        //获取分类集合
        Map<Integer, List<CategoryEntity>> map = this.getcateList(aggregations);

        List<CategoryEntity> categoryList = null;
        Integer hotCid = 0;

        for (Map.Entry<Integer, List<CategoryEntity>> mapEntry : map.entrySet()) {
            hotCid = mapEntry.getKey();
            categoryList = mapEntry.getValue();
        }

        //获取品牌集合
        List<BrandEntity> brandList = this.getBrandList(aggregations);

        //通过cid查询规格参数
        Map<String, List<String>> specParamVlaueMap = this.getspecParam(hotCid, search);

        return new GoodsResponse(total, totalPage, brandList, categoryList, goodsDocList, specParamVlaueMap);

    }

    private Map<String, List<String>> getspecParam(Integer hotCid, String search) {

        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(hotCid);
        specParamDTO.setSearching(true);//只搜索有查询属性的规格参数
        Result<List<SpecParamEntity>> specParamInfo = specificationFeign.getSpecParamInfo(specParamDTO);

        if (specParamInfo.getCode() == 200) {

            List<SpecParamEntity> specParamList = specParamInfo.getData();
            //聚合查询
            NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
            searchQueryBuilder.withQuery(QueryBuilders.multiMatchQuery(search, "title", "brandName", "categoryName"));

            //分页 必须得查询一条数据
            searchQueryBuilder.withPageable(PageRequest.of(0, 1));

            specParamList.stream().forEach(specParam -> {
                searchQueryBuilder.addAggregation(AggregationBuilders.terms(specParam.getName()).field("specs." + specParam.getName() + ".keyword"));
            });

            SearchHits<GoodsDoc> search1 = elasticsearchRestTemplate.search(searchQueryBuilder.build(), GoodsDoc.class);

            Map<String, List<String>> map = new HashMap<>();
            Aggregations aggregations = search1.getAggregations();

            specParamList.stream().forEach(specparam -> {
                Terms terms = aggregations.get(specparam.getName());

                List<? extends Terms.Bucket> buckets = terms.getBuckets();

                List<String> valueList = buckets.stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());

                map.put(specparam.getName(), valueList);
            });
            return map;
        }
        return null;
    }

    private NativeSearchQueryBuilder getSearchQuery(String search, Integer page,String filter) {

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //多字段查询
        queryBuilder.withQuery(QueryBuilders.multiMatchQuery(search, "title", "brandName", "categoryName"));

        //高亮
        queryBuilder.withHighlightBuilder(ESHighLightUtil.getHighlightBuilder("title"));

        //分页
        queryBuilder.withPageable(PageRequest.of(page - 1, 10));

        //聚合
        queryBuilder.addAggregation(AggregationBuilders.terms("cate_agg").field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms("brand_agg").field("brandId"));

        if(!StringUtil.isEmpty(filter) && filter.length() >2){
            //与或非
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            Map<String, String> filterMap = JSONUtil.toMapValueString(filter);

            for(Map.Entry<String,String> item : filterMap.entrySet()){
                MatchQueryBuilder matchQueryBuilder = null;
                //分类 品牌 和规格参数的查询方式不一样
                if(item.getKey().equals("cid3") || item.getKey().equals("brandId")) {
                    matchQueryBuilder = QueryBuilders.matchQuery(item.getKey(),item.getValue());
                }else{
                    matchQueryBuilder = QueryBuilders.matchQuery("specs." + item.getKey() + ".keyword",item.getValue());
                }

                boolQueryBuilder.must(matchQueryBuilder);
            }
            //添加过滤,过滤不会影响评分
            queryBuilder.withFilter(boolQueryBuilder);
        }

        return queryBuilder;
    }

    private List<BrandEntity> getBrandList(Aggregations aggregations) {

        Terms brand_agg = aggregations.get("brand_agg");

        List<? extends Terms.Bucket> brandBuckets = brand_agg.getBuckets();
        List<String> brandIdList = brandBuckets.stream().map(brandBucket -> {

            Number keyAsNumber = brandBucket.getKeyAsNumber();
            Integer brandId = Integer.valueOf(keyAsNumber.intValue());
            //得到品牌id,并且且转为String类型,方便接下来的操作
            return brandId + "";

        }).collect(Collectors.toList());

        //通过brandid获取brand详细数据
        //String.join(分隔符,List<String>),将list集合转为,分隔的字符串
        Result<List<BrandEntity>> brandResult = brandFeign.getBrandByIds(String.join(",", brandIdList));

        return brandResult.getData();
    }

    private Map<Integer, List<CategoryEntity>> getcateList(Aggregations aggregations) {

        Terms cate_agg = aggregations.get("cate_agg");

        List<? extends Terms.Bucket> cateBuckets = cate_agg.getBuckets();

        Map<Integer, List<CategoryEntity>> map = new HashMap<>();

        List<Integer> hotCidList = Arrays.asList(0);//热度最高的分类id
        List<Integer> maxCountList = Arrays.asList(0);

        List<String> cateIdList = cateBuckets.stream().map(cateBucket -> {

            Number keyAsNumber = cateBucket.getKeyAsNumber();
            Integer cateId = Integer.valueOf(keyAsNumber.intValue());

            if (maxCountList.get(0) < cateBucket.getDocCount()) {

                maxCountList.set(0, Long.valueOf(cateBucket.getDocCount()).intValue());
                hotCidList.set(0, cateId);
            }

            return cateId + "";
        }).collect(Collectors.toList());

        //通过分类id获取分类详细数据
        Result<List<CategoryEntity>> cateResult = categoryFeign.getCateByIds(String.join(",", cateIdList));

        map.put(hotCidList.get(0), cateResult.getData());

        return map;
    }

    @Override
    public Result<JSONObject> clearGoodsEsData() {

        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(GoodsDoc.class);

        if (indexOperations.exists()) {
            indexOperations.delete();
            System.out.println("索引删除成功");
        }

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> initGoodsEsData() {

        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(GoodsDoc.class);

        if (!indexOperations.exists()) {
            indexOperations.create();
            indexOperations.createMapping();
            System.out.println("索引创建成功");
        }

        //批量新增数据
        List<GoodsDoc> goodsDocs = this.esGoodsInfo(new SpuDTO() );

        elasticsearchRestTemplate.save(goodsDocs);

        return this.setResultSuccess();
    }

    private List<GoodsDoc> esGoodsInfo(SpuDTO spuDTO) {

        //SpuDTO spuDTO = new SpuDTO();
        /*spuDTO.setPage(1);
        spuDTO.setRows(5);*/


        List<GoodsDoc> goodsDocs = new ArrayList<>();

        Result<List<SpuDTO>> spuInfo = goodsFeign.getSpuInfo(spuDTO);

        if (spuInfo.getCode() == HTTPStatus.OK) {

            List<Long> priceList = new ArrayList<>();

            List<SpuDTO> spuDTOList = spuInfo.getData();

            spuDTOList.stream().forEach(spu -> {

                GoodsDoc goodsDoc = new GoodsDoc();
                Integer spuId = spu.getId();

                //spu信息填充
                goodsDoc.setId(spuId.longValue());
                goodsDoc.setCid1(spu.getCid1().longValue());
                goodsDoc.setCid2(spu.getCid2().longValue());
                goodsDoc.setCid3(spu.getCid3().longValue());
                goodsDoc.setCreateTime(spu.getCreateTime());
                goodsDoc.setSubTitle(spu.getSubTitle());
                //可搜索的数据
                goodsDoc.setTitle(spu.getTitle());
                goodsDoc.setBrandId(spu.getBrandId().longValue());
                goodsDoc.setBrandName(spu.getBrandName());
                goodsDoc.setCategoryName(spu.getCategoryName());

                //通过spuID查询skuList
                Map<List<Long>, List<Map<String, Object>>> skus = this.getSkusAndPriceList(spu.getId());
                skus.forEach((key, value) -> {
                    goodsDoc.setPrice(key);
                    goodsDoc.setSkus(JSONUtil.toJsonString(value));
                });

                //通过cid3查询规格参数
                Map<String, Object> specs = this.getSpecMap(spu);

                goodsDoc.setSpecs(specs);
                goodsDocs.add(goodsDoc);
            });
        }


        return goodsDocs;
    }

    private Map<List<Long>, List<Map<String, Object>>> getSkusAndPriceList(Integer spuId) {

        Map<List<Long>, List<Map<String, Object>>> hashMap = new HashMap<>();

        //通过spuID查询skuList
        Result<List<SkuDTO>> skuResult = goodsFeign.getSkuBySpuId(spuId);

        //创建list
        List<Long> priceList = new ArrayList<>();

        //创建map
        List<Map<String, Object>> skuMap = null;

        if (skuResult.getCode() == HTTPStatus.OK) {

            List<SkuDTO> skuList = skuResult.getData();

            //遍历sku数据
            skuMap = skuList.stream().map(sku -> {

                //sku数据放入map中
                Map<String, Object> map = new HashMap<>();
                map.put("id", sku.getId());
                map.put("title", sku.getTitle());
                map.put("image", sku.getImages());
                map.put("price", sku.getPrice());

                //sku价格 集合
                priceList.add(sku.getPrice().longValue());

                return map;
            }).collect(Collectors.toList());

        }

        hashMap.put(priceList, skuMap);
        return hashMap;
    }


    private Map<String, Object> getSpecMap(SpuDTO spuDTO) {

        //规格数据填充
        //获取规格参数
        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(spuDTO.getCid3());
        specParamDTO.setSearching(true);

        //查询规格参数
        Result<List<SpecParamEntity>> specParamInfo = specificationFeign.getSpecParamInfo(specParamDTO);

        //遍历规格参数数据
        Map<String, Object> specs = new HashMap<>();

        if (specParamInfo.getCode() == HTTPStatus.OK) {

            //获取Spudetail数据 获取spu详细信息
            Result<SpuDetailEntity> spuDetailBydSpu = goodsFeign.getSpuDetailBydSpu(spuDTO.getId());
            if (spuDetailBydSpu.getCode() == HTTPStatus.OK) {

                SpuDetailEntity spuDetail = spuDetailBydSpu.getData();

                //将通用规格转换为map
                String genericSpec = spuDetail.getGenericSpec();
                Map<String, String> genericSpecMap = JSONUtil.toMapValueString(genericSpec);

                //特殊规格转换为map,值为List,因为有可能会有多个  比如 颜色
                String specialSpec = spuDetail.getSpecialSpec();
                Map<String, List<String>> stringListMap = JSONUtil.toMapValueStrList(specialSpec);

                specParamInfo.getData().stream().forEach(specParam -> {

                    //将对应的规格名称和值放入到map集合中
                    if (specParam.getGeneric()) {//通用规格

                        if (specParam.getNumeric() && specParam.getSearching()) {
                            String value = genericSpecMap.get(specParam.getId() + "");

                            specs.put(specParam.getName(), this.chooseSegment(value, specParam.getSegments(), specParam.getUnit()));
                        } else {
                            specs.put(specParam.getName(), genericSpecMap.get(specParam.getId() + ""));
                        }


                    } else {//特殊规格
                        specs.put(specParam.getName(), stringListMap.get(specParam.getId() + ""));
                    }
                });


            }

        }

        return specs;
    }


    private String chooseSegment(String value, String segments, String unit) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : segments.split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + unit + "以上";
                } else if (begin == 0) {
                    result = segs[1] + unit + "以下";
                } else {
                    result = segment + unit;
                }
                break;
            }
        }
        return result;
    }

}
