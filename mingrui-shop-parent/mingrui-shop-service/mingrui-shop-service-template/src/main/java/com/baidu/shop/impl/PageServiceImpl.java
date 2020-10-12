package com.baidu.shop.impl;

import com.baidu.shop.dto.*;
import com.baidu.shop.entity.*;
import com.baidu.feign.BrandFeign;
import com.baidu.feign.CategoryFeign;
import com.baidu.feign.GoodsFeign;
import com.baidu.feign.SpecificationFeign;
import com.baidu.base.Result;
import com.baidu.shop.service.PageService;
import com.baidu.utils.BaiduBeanUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName PageServiceImpl
 * @Description: TODO
 * @Author gengzifang
 * @Date 2020/9/23
 * @Version V1.0
 **/
@Service
public class PageServiceImpl implements PageService {

    //@Autowired
    private GoodsFeign goodsFeign;

    //@Autowired
    private BrandFeign brandFeign;

    //@Autowired
    private CategoryFeign categoryFeign;

    //@Autowired
    private SpecificationFeign specificationFeign;


    @Override
    public Map<String, Object> getPageInfoBySpuId(Integer spuId) {

        Map<String, Object> map = new HashMap<>();

        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setId(spuId);

        Result<List<SpuDTO>> spuInfoResult = goodsFeign.getSpuInfo(spuDTO);

        if (spuInfoResult.getCode() == 200) {
            if (spuInfoResult.getData().size() == 1) {

                SpuDTO spuInfo = spuInfoResult.getData().get(0);

                map.put("spuInfo",spuInfo);

                //查询分类信息
                List<String> cateCids = Arrays.asList(spuInfo.getCid1() + "", spuInfo.getCid2() + "", spuInfo.getCid3() + "");
                String cateCidString = String.join(",", cateCids);
                Result<List<CategoryEntity>> cateResult = categoryFeign.getCateByIds(cateCidString);
                if(cateResult.getCode() == 200){
                    map.put("cateList",cateResult.getData());
                }

                //查询spuDetail信息
                Result<SpuDetailEntity> spuDetailBydSpu = goodsFeign.getSpuDetailBydSpu(spuId);
                if(spuDetailBydSpu.getCode() == 200){
                    SpuDetailEntity spuDetailInfo = spuDetailBydSpu.getData();
                    map.put("spuDetailInfo",spuDetailInfo);
                }

                //查询特有规格参数
                SpecParamDTO specParamDTO = new SpecParamDTO();
                specParamDTO.setCid(spuInfo.getCid3());
                specParamDTO.setGeneric(false);
                Result<List<SpecParamEntity>> specParamResult = specificationFeign.getSpecParamInfo(specParamDTO);
                if(specParamResult.getCode() == 200){
                    HashMap<Integer, String> specMap = new HashMap<>();
                    specParamResult.getData().stream().forEach(spec ->{
                        specMap.put(spec.getId(),spec.getName());
                        map.put("specParamMap",specMap);
                    });
                }

                //查询规格组和规格参数
                SpecGroupDTO specGroupDTO = new SpecGroupDTO();
                specGroupDTO.setCid(spuInfo.getCid3());
                Result<List<SpecGroupEntity>> sepcGroupResult = specificationFeign.getSepcGroupInfo(specGroupDTO);
                if(sepcGroupResult.getCode() == 200){
                    List<SpecGroupEntity> specGroupdata = sepcGroupResult.getData();

                    //规格组和规格参数
                    List<SpecGroupDTO> specGroupDTOList  = specGroupdata.stream().map(specGroup -> {

                        SpecGroupDTO sgd = BaiduBeanUtil.copyProperties(specGroup, SpecGroupDTO.class);
                        //规格参数  通用参数
                        SpecParamDTO specParamDTO1 = new SpecParamDTO();
                        specParamDTO1.setGroupId(sgd.getId());
                        specParamDTO1.setGeneric(true);
                        Result<List<SpecParamEntity>> specParamResult1 = specificationFeign.getSpecParamInfo(specParamDTO1);

                        if (specParamResult1.getCode() == 200) {
                            sgd.setParamList(specParamResult1.getData());
                        }
                        return sgd;
                    }).collect(Collectors.toList());

                    map.put("specGroupDTOList",specGroupDTOList);
                }


                //查询sku信息
                Result<List<SkuDTO>> skuResult = goodsFeign.getSkuBySpuId(spuId);
                if(skuResult.getCode() == 200){
                    List<SkuDTO> skuList = skuResult.getData();
                    map.put("skuList",skuList);
                }


                //品牌信息
                BrandDTO brandDTO = new BrandDTO();
                brandDTO.setId(spuInfo.getBrandId());
                Result<PageInfo<BrandEntity>> brandInfo = brandFeign.
                        getBrandInfo(brandDTO);

                if (brandInfo.getCode() == 200) {
                    PageInfo<BrandEntity> data = brandInfo.getData();

                    List<BrandEntity> list = data.getList();
                    if (list.size() == 1) {
                        map.put("brandInfo", list.get(0));
                    }
                }

            }
        }

        return map;
    }
}
