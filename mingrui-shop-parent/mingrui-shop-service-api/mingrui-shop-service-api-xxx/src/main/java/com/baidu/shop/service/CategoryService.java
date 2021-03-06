package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.base.Result;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.validate.group.MingruiOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品分类接口")
public interface CategoryService {

    @ApiOperation(value = "通过查询商品分类")
    @GetMapping(value = "category/list")
    Result<List<CategoryEntity>> getCategoryByPid(Integer pid);


    @ApiOperation(value = "新增分类")
    @PostMapping(value = "category/add")
    //声明哪个组下面的参数参加校验-->当前是校验新增组
    Result<JSONObject> saveCategory(@Validated({MingruiOperation.Add.class}) @RequestBody CategoryEntity categoryEntity);

    @ApiOperation(value = "修改分类")
    @PutMapping(value = "category/edit")
    Result<JSONObject> editCategory(@Validated({MingruiOperation.Update.class}) @RequestBody CategoryEntity categoryEntity);

    @ApiOperation(value = "删除分类")
    @DeleteMapping(value = "category/delete")
    Result<JSONObject> deleteCategory(Integer id);

    @ApiOperation(value = "通过品牌id查询商品分类")
    @GetMapping(value = "category/getByBrand")
    Result<List<CategoryEntity>> getByBrand(Integer brandId);

    @ApiOperation(value = "通过分类id集合获取分类信息")
    @GetMapping(value = "category/getCateByIds")
    Result<List<CategoryEntity>> getCateByIds(@RequestParam String cateIds);

}
