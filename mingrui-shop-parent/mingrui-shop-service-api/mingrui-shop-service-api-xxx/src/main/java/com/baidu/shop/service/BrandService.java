package com.baidu.shop.service;

import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.base.Result;
import com.baidu.validate.group.MingruiOperation;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "品牌接口")
public interface BrandService {

    @ApiOperation(value = "获取品牌信息List格式")
    @GetMapping(value = "brand/getBrandInfoList")
    public Result<List<BrandEntity>> getBrandInfoList(@SpringQueryMap BrandDTO brandDTO);

    @ApiOperation(value = "获取品牌信息")
    @GetMapping(value = "brand/getBrandInfo")
    Result<PageInfo<BrandEntity>> getBrandInfo(@SpringQueryMap BrandDTO brandDTO);

    @ApiOperation(value = "品牌新增")
    @PostMapping(value = "brand/save")
    Result<JsonObject> saveBrandInfo(@Validated(MingruiOperation.Add.class) @RequestBody BrandDTO brandDTO);

    @ApiOperation(value = "品牌修改")
    @PutMapping(value = "brand/save")
    Result<JsonObject> editBrand(@Validated(MingruiOperation.Update.class) @RequestBody BrandDTO brandDTO);

    @ApiOperation(value = "品牌删除")
    @DeleteMapping(value = "brand/delete")
    Result<JsonObject> deleteBrand(Integer id);

    @ApiOperation(value = "根据分类id查询品牌")
    @GetMapping(value = "brand/getBrandByCategory")
    Result<List<BrandEntity>> getBrandByCategory(Integer cid);

    @ApiOperation(value = "通过品牌id集合获取品牌")
    @GetMapping(value = "brand/getBrandByIds")
    Result<List<BrandEntity>> getBrandByIds(@RequestParam String brandIds);
}
