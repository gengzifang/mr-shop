package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.*;
import com.baidu.shop.mapper.*;
import com.baidu.shop.service.GoodsService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.ObjectUtil;
import com.baidu.shop.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;;
import java.util.stream.Collectors;

/**
 * @ClassName GoodsServiceImpl
 * @Description: TODO
 * @Author gengzifang
 * @Date 2020/9/7
 * @Version V1.0
 **/
@RestController
public class GoodsServiceImpl extends BaseApiService implements GoodsService {

    @Resource
    private SpuMapper spuMapper;

    @Resource
    private BrandMapper brandMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SpuDetailMapper spuDetailMapper;

    @Resource
    private SkuMapper skuMapper;

    @Resource
    private StockMapper stockMapper;


    @Transactional
    @Override
    public Result<JSONObject> GoodsSave(SpuDTO spuDTO) {

        //新增spu
        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setSaleable(1);
        spuEntity.setValid(1);
        final Date date = new Date();
        spuEntity.setCreateTime(date);
        spuEntity.setLastUpdateTime(date);

        spuMapper.insertSelective(spuEntity);

        //spu新增返回主键
        Integer spuEntityId = spuEntity.getId();

        //新增spuDetail
        SpuDetailEntity spuDetailEntity = BaiduBeanUtil.copyProperties(spuDTO.getSpuDetail(), SpuDetailEntity.class);
        spuDetailEntity.setSpuId(spuEntityId);

        spuDetailMapper.insertSelective(spuDetailEntity);

        //新增sku
        this.saveSkusAndStocks(spuDTO.getSkus(), spuEntityId, date);

        return this.setResultSuccess();
    }

    @Override
    public Result<SpuDetailEntity> getSpuDetailBydSpu(Integer spuId) {

        SpuDetailEntity spuDetailEntity = spuDetailMapper.selectByPrimaryKey(spuId);

        return this.setResultSuccess(spuDetailEntity);
    }

    @Override
    public Result<SkuDTO> getSkuBySpuId(Integer spuId) {

        List<SkuDTO> list = skuMapper.selectSkuAndStockBySpuId(spuId);

        return this.setResultSuccess(list);
    }

    @Transactional
    @Override
    public Result<JSONObject> editGoods(SpuDTO spuDTO) {

        //修改spu
        Date date = new Date();
        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setLastUpdateTime(date);

        spuMapper.updateByPrimaryKeySelective(spuEntity);
        //修改spuDetail
        SpuDetailEntity spuDetailEntity = BaiduBeanUtil.copyProperties(spuDTO.getSpuDetail(), SpuDetailEntity.class);

        spuDetailMapper.updateByPrimaryKeySelective(spuDetailEntity);

        //通过spuId查询出来将要被删除的sku
        //获取所有将要被删除skuId(如果直接删除的话stock没有办法删除)
        List<Long> skuIdArrBySpuId = this.getSkuIdArrBySpuId(spuDTO.getId());

        if (skuIdArrBySpuId.size() > 0) {

            //批量删除sku
            skuMapper.deleteByIdList(skuIdArrBySpuId);

            //批量删除stock
            stockMapper.deleteByIdList(skuIdArrBySpuId);
        }

        //将新的数据新增到数据库
        this.saveSkusAndStocks(spuDTO.getSkus(), spuDTO.getId(), date);

        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JSONObject> delGoods(Integer spuId) {

        //删除spu
        spuMapper.deleteByPrimaryKey(spuId);

        //删除spuDetail
        spuDetailMapper.deleteByPrimaryKey(spuId);

        //查询
        List<Long> skuIdArrs = this.getSkuIdArrBySpuId(spuId);

        if (skuIdArrs.size() > 0) {//避免全表数据被删除
            //删除sku
            skuMapper.deleteByIdList(skuIdArrs);

            //删除stock
            stockMapper.deleteByIdList(skuIdArrs);
        }

        return this.setResultSuccess();
    }

    public List<Long> getSkuIdArrBySpuId(Integer spuId) {

        Example example = new Example(SkuEntity.class);
        example.createCriteria().andEqualTo("spuId", spuId);
        List<SkuEntity> skuEntities = skuMapper.selectByExample(example);
        List<Long> collect = skuEntities.stream().map(sku -> sku.getId()).collect(Collectors.toList());

        return collect;
    }


    public void saveSkusAndStocks(List<SkuDTO> skus, Integer spuId, Date date) {
        //新增sku
        skus.stream().forEach(skuDTO -> {
            SkuEntity skuEntity = BaiduBeanUtil.copyProperties(skuDTO, SkuEntity.class);
            skuEntity.setSpuId(spuId);
            skuEntity.setCreateTime(date);
            skuEntity.setLastUpdateTime(date);

            skuMapper.insertSelective(skuEntity);

            //新增Stock
            StockEntity stockEntity = new StockEntity();
            stockEntity.setSkuId(skuEntity.getId());
            stockEntity.setStock(skuDTO.getStock());

            stockMapper.insertSelective(stockEntity);
        });
    }

    @Transactional
    @Override
    public Result<PageInfo<SpuEntity>> getSpuInfo(SpuDTO spuDTO) {

        //分页
        if (null != spuDTO.getPage() && null != spuDTO.getRows()) {
            PageHelper.startPage(spuDTO.getPage(), spuDTO.getRows());
        }

        //条件查询
        Example example = new Example(SpuEntity.class);
        //查询条件
        Example.Criteria criteria = example.createCriteria();

        if (ObjectUtil.isNotNull(spuDTO)) {
            //按标题查询
            if (StringUtil.isNotEmpty(spuDTO.getTitle())) {
                criteria.andLike("title", "%" + spuDTO.getTitle() + "%");
            }
            //上下架 2不拼接  查询所有
            if (ObjectUtil.isNotNull(spuDTO.getSaleable()) && spuDTO.getSaleable() != 2) {
                criteria.andEqualTo("saleable", spuDTO.getSaleable());
            }
            //排序
            if (!StringUtil.isEmpty(spuDTO.getOrder())) {
                example.setOrderByClause(spuDTO.getOrderByClause());
            }

        }

        List<SpuEntity> list = spuMapper.selectByExample(example);

        List<SpuDTO> spuDtoList = list.stream().map(spuEntity -> {

            //通过品牌id得到品牌名称
            BrandEntity brandEntity = brandMapper.selectByPrimaryKey(spuEntity.getBrandId());

            //List<BrandEntity> brandEntity = spuMapper.getbrandEntity(spuEntity.getBrandId());


            //分类名称 通过cid1 cid2 cid3
            List<CategoryEntity> categoryList = categoryMapper.selectByIdList(Arrays.asList(spuEntity.getCid1(), spuEntity.getCid2(), spuEntity.getCid3()));
            String caterogyName = categoryList.stream().map(category -> category.getName()).collect(Collectors.joining("/"));

            //List<CategoryEntity> category = categoryMapper.getcCategory(spuEntity.getCid1(),spuEntity.getCid2(),spuEntity.getCid3());

            SpuDTO spuDTO1 = BaiduBeanUtil.copyProperties(spuEntity, SpuDTO.class);

            spuDTO1.setCategoryName(caterogyName);

            spuDTO1.setBrandName(brandEntity.getName());

            return spuDTO1;
        }).collect(Collectors.toList());


        PageInfo<SpuEntity> info = new PageInfo<>(list);

        long total = info.getTotal();

        return this.setResult(HTTPStatus.OK, total + "", spuDtoList);
    }

}
