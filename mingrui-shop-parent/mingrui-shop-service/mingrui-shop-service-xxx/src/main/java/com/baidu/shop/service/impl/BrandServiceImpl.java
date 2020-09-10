package com.baidu.shop.service.impl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryBrandEntity;
import com.baidu.shop.entity.SpuEntity;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.shop.mapper.CategoryBrandMapper;
import com.baidu.shop.mapper.SpuMapper;
import com.baidu.shop.service.BrandService;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.ObjectUtil;
import com.baidu.shop.utils.PinyinUtil;
import com.baidu.shop.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName BrandServiceImpl
 * @Description: TODO
 * @Author gengzifang
 * @Date 2020/8/31
 * @Version V1.0
 **/
@RestController
public class BrandServiceImpl extends BaseApiService implements BrandService {

    @Resource
    private BrandMapper brandMapper;

    @Resource
    private CategoryBrandMapper categoryBrandMapper;

    @Resource
    private SpuMapper spuMapper;


    @Override
    public Result<List<BrandEntity>> getBrandByCategory(Integer cid) {

        if (ObjectUtil.isNotNull(cid)) {

            List<BrandEntity> list = brandMapper.getBrandByCategory(cid);

            return this.setResultSuccess(list);
        }
        return null;
    }

    @Override
    public Result<PageInfo<BrandEntity>> getBrandInfo(BrandDTO brandDTO) {

        //分页
        PageHelper.startPage(brandDTO.getPage(), brandDTO.getRows());

        Example example = new Example(BrandEntity.class);

        //排序
        if (null != brandDTO.getOrder()) {
            example.setOrderByClause(brandDTO.getOrderByClause());
        }
        //多条件
        if (null != brandDTO.getName()) {
            example.createCriteria().andLike("name", "%" + brandDTO.getName() + "%");
        }

        //查询
        List<BrandEntity> list = brandMapper.selectByExample(example);

        PageInfo<BrandEntity> pageInfo = new PageInfo<>(list);

        return this.setResultSuccess(pageInfo);
    }

    @Transactional
    @Override
    public Result<JsonObject> saveBrandInfo(BrandDTO brandDTO) {

        //实例
        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO, BrandEntity.class);

        //获取到品牌名称
        //获取到品牌名称第一个字符
        //将第一个字符转换为pinyin
        //获取拼音的首字母
        //统一转为大写
        String name = brandEntity.getName();
        char c = name.charAt(0);
        String upperCase = PinyinUtil.getUpperCase(String.valueOf(c), PinyinUtil.TO_FIRST_CHAR_PINYIN);
        brandEntity.setLetter(upperCase.charAt(0));

        //增加
        brandMapper.insertSelective(brandEntity);


        this.insert(brandDTO, brandEntity);

        return this.setResultSuccess();
    }

    @Override
    public Result<JsonObject> editBrand(BrandDTO brandDTO) {

        //实例
        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO, BrandEntity.class);
        //大写首字母
        String name = brandEntity.getName();
        char c = name.charAt(0);
        String upperCase = PinyinUtil.getUpperCase(String.valueOf(c), PinyinUtil.TO_FIRST_CHAR_PINYIN);
        brandEntity.setLetter(upperCase.charAt(0));

        brandMapper.updateByPrimaryKeySelective(brandEntity);

        //通过品牌id删除关系表中的关系
        this.deletes(brandEntity.getId());

        //删除完毕再新增
        this.insert(brandDTO, brandEntity);

        return this.setResultSuccess();
    }

    @Override
    public Result<JsonObject> deleteBrand(Integer id) {

        Example example = new Example(SpuEntity.class);
        example.createCriteria().andEqualTo("brandId", id);

        List<SpuEntity> spuEntities = spuMapper.selectByExample(example);
        if (spuEntities.size() >= 1) {
            return this.setResultError("该品牌绑定有商品,不能删除");
        }


        //删除品牌
        brandMapper.deleteByPrimaryKey(id);

        //删除关系表中的关系
        this.deletes(id);

        return this.setResultSuccess();
    }

    private void deletes(Integer id) {

        //通过品牌id删除关系表中的关系
        Example example = new Example(CategoryBrandEntity.class);
        example.createCriteria().andEqualTo("brandId", id);
        categoryBrandMapper.deleteByExample(example);

    }

    private void insert(BrandDTO brandDTO, BrandEntity brandEntity) {

        //判断品牌分类是否包含,
        if (brandDTO.getCategory().contains(",")) {
            //通过split方法分割字符串的Array
            //Arrays.asList将Array转换为List
            //使用JDK1,8的stream
            //使用map函数返回一个新的数据
            //collect 转换集合类型Stream<T>
            //Collectors.toList())将集合转换为List类型

            List<CategoryBrandEntity> categoryBrandEntities = Arrays.asList(brandDTO.getCategory().split(","))
                    .stream().map(cid -> {

                        CategoryBrandEntity entity = new CategoryBrandEntity();
                        entity.setCategoryId(StringUtil.toInteger(cid));
                        entity.setBrandId(brandEntity.getId());

                        return entity;
                    }).collect(Collectors.toList());

            //批量新增
            categoryBrandMapper.insertList(categoryBrandEntities);

        } else {
            //新增
            CategoryBrandEntity entity = new CategoryBrandEntity();

            entity.setCategoryId(StringUtil.toInteger(brandDTO.getCategory()));
            entity.setBrandId(brandEntity.getId());

            categoryBrandMapper.insertSelective(entity);
        }

    }
}
