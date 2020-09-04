package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.entity.*;
import com.baidu.shop.mapper.*;
import com.baidu.shop.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName CategoryServiceImpl
 * @Description: TODO
 * @Author gengzifang
 * @Date 2020/8/27
 * @Version V1.0
 **/
@RestController
public class CategoryServiceImpl extends BaseApiService implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Resource
    private CategoryBrandMapper categoryBrandMapper;

    @Resource
    private SpecGroupMapper specGroupMapper;

    @Resource
    private BrandMapper brandMapper;


    @Override
    public Result<List<CategoryEntity>> getCategoryByPid(Integer pid) {

        CategoryEntity categoryEntity = new CategoryEntity();

        categoryEntity.setParentId(pid);

        List<CategoryEntity> list = categoryMapper.select(categoryEntity);

        Result<List<CategoryEntity>> listResult = new Result<>();

        return this.setResultSuccess(list);
    }

    @Override
    public Result<JSONObject> saveCategory(CategoryEntity categoryEntity) {

        //通过新增节点的父id将父节点的parent状态改为1

        CategoryEntity parentCategory = new CategoryEntity();
        parentCategory.setId(categoryEntity.getParentId());
        parentCategory.setIsParent(1);

        categoryMapper.updateByPrimaryKeySelective(parentCategory);

        categoryMapper.insertSelective(categoryEntity);

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> editCategory(CategoryEntity categoryEntity) {

        categoryMapper.updateByPrimaryKeySelective(categoryEntity);

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> deleteCategory(Integer id) {

        //通过当前id查询分类信息
        CategoryEntity categoryEntity = categoryMapper.selectByPrimaryKey(id);
        if (null == categoryEntity.getId()) {
            return this.setResultError("当前id不存在");
        }
        //不能查询到分类信息直接return
        //如果能查到 判断当前节点是不是父节点
        if (categoryEntity.getIsParent() == 1) {
            return this.setResultError("当前节点为父节点,不能删除");
        }
        //通过分类id查询  关系表中  和品牌的关系
        Example example1 = new Example(CategoryBrandEntity.class);
        example1.createCriteria().andEqualTo("categoryId", id);
        List<CategoryBrandEntity> list1 = categoryBrandMapper.selectByExample(example1);
        //判断是否有和品牌的绑定
        //如果有返回信息

        //不能打印绑定的品牌名称 太多了
        if (list1.size() >= 1) {
            return this.setResultError("该分类绑定有品牌不能删除");
        }

        //通过分类id查询  关系表中  和规格的关系
        Example example2 = new Example(SpecGroupEntity.class);
        example2.createCriteria().andEqualTo("cid", id);
        List<SpecGroupEntity> list2 = specGroupMapper.selectByExample(example2);
        //判断是否有和规格的绑定
        //如果有返回信息

        if (list2.size() >= 1) {
            String mag = "";
            for (SpecGroupEntity spec : list2) {
                mag += "," + spec.getName();
            }

            return this.setResultError("该分类绑定有规格" + "(" + mag  + ")" + "不能删除");
        }


        //还需要判断一下除了当前被删除的id外还有没有父id是当前节点的数据
        Example example = new Example(CategoryEntity.class);

        example.createCriteria().andEqualTo("parentId", categoryEntity.getParentId());

        List<CategoryEntity> list = categoryMapper.selectByExample(example);

        if (list.size() == 1) {
            CategoryEntity categoryEntity1 = new CategoryEntity();
            categoryEntity1.setId(categoryEntity.getParentId());
            categoryEntity1.setIsParent(0);
            categoryMapper.updateByPrimaryKeySelective(categoryEntity1);
        }

        //如果只查询出来一条数据的话,通过ParentId将父节点的isParent状态改为0
        //没有的话将父节点ParentId改为0


        categoryMapper.deleteByPrimaryKey(id);

        return this.setResultSuccess();
    }

    @Override
    public Result<List<CategoryEntity>> getByBrand(Integer brandId) {

        List<CategoryEntity> list = categoryMapper.getByBrand(brandId);


        return this.setResultSuccess(list);
    }
}
