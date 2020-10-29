package com.baidu.shop.service.impl;

import com.baidu.base.BaseApiService;
import com.baidu.base.Result;
import com.baidu.shop.dto.SlideshowyDTO;
import com.baidu.shop.entity.SkuEntity;
import com.baidu.shop.entity.SlideshowyEntity;
import com.baidu.shop.entity.SpuEntity;
import com.baidu.shop.mapper.SkuMapper;
import com.baidu.shop.mapper.SlideshowyMapper;
import com.baidu.shop.mapper.SpuMapper;
import com.baidu.shop.service.SlideshowyService;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


/**
 * @ClassName SlideshowyImpl
 * @Description: TODO
 * @Author gengzifang
 * @Date 2020/10/27
 * @Version V1.0
 **/
@RestController
public class SlideshowyImpl extends BaseApiService implements SlideshowyService {

    @Resource
    private SlideshowyMapper slideshowyMapper;

    @Resource
    private SpuMapper spuMapper;

    @Resource
    private SkuMapper skuMapper;

    @Override
    public Result<JSONObject> saveslideshowy(SlideshowyDTO slideshowyDTO) {

        if (slideshowyDTO.getSpuId() != null) {
            SpuEntity spuEntity = spuMapper.selectByPrimaryKey(slideshowyDTO.getSpuId());

            Example example = new Example(SkuEntity.class);
            example.createCriteria().andEqualTo("spuId", spuEntity.getId());

            List<SkuEntity> skuEntities = skuMapper.selectByExample(example);


            String s = skuEntities.get(0).getImages();

            SlideshowyEntity slideshowyEntity = new SlideshowyEntity();

            Date date = new Date();

            slideshowyEntity.setSpuId(spuEntity.getId());
            slideshowyEntity.setTitle(spuEntity.getTitle());
            slideshowyEntity.setCreateTime(date);
            slideshowyEntity.setImg(s);
            slideshowyEntity.setDelFlag(0);

            slideshowyMapper.insertSelective(slideshowyEntity);

        }

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> delslideshowy(Integer id) {

        if (id != null) {

            SlideshowyEntity slideshowyEntity = new SlideshowyEntity();
            slideshowyEntity.setDelFlag(1);
            slideshowyEntity.setId(id);

            slideshowyMapper.updateByPrimaryKeySelective(slideshowyEntity);

            //slideshowyMapper.deleteByPrimaryKey(id);
        }

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> updateslideshowy(SlideshowyDTO slideshowyDTO) {

        if (slideshowyDTO != null) {

            SpuEntity spuEntity = spuMapper.selectByPrimaryKey(slideshowyDTO.getSpuId());

            Example example = new Example(SkuEntity.class);
            example.createCriteria().andEqualTo("spuId", spuEntity.getId());

            List<SkuEntity> skuEntities = skuMapper.selectByExample(example);

            String s = skuEntities.get(0).getImages();

            SlideshowyEntity slideshowyEntity = new SlideshowyEntity();

            Date date = new Date();

            slideshowyEntity.setId(slideshowyDTO.getId());
            slideshowyEntity.setSpuId(spuEntity.getId());
            slideshowyEntity.setTitle(spuEntity.getTitle());
            slideshowyEntity.setCreateTime(date);
            slideshowyEntity.setImg(s);
            slideshowyEntity.setDelFlag(0);

            slideshowyMapper.updateByPrimaryKeySelective(slideshowyEntity);

        }

        return this.setResultSuccess();
    }

    @Override
    public Result<List<SlideshowyEntity>> getslideshowy() {

        List<SlideshowyEntity> list = slideshowyMapper.selectdelFlag();

        PageInfo<SlideshowyEntity> info = new PageInfo<>(list);

        return this.setResultSuccess(info);
    }
}
