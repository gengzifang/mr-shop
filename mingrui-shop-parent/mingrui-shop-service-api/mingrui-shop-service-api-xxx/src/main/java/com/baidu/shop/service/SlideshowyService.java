package com.baidu.shop.service;

import com.baidu.shop.dto.SlideshowyDTO;
import com.baidu.shop.entity.SlideshowyEntity;
import io.swagger.annotations.Api;

import com.baidu.base.Result;
import io.swagger.annotations.ApiOperation;
import org.json.JSONObject;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "轮播图接口")
public interface SlideshowyService {

    @ApiOperation(value = "轮播图增加")
    @PostMapping(value = "slideshowy/save")
    Result<JSONObject> saveslideshowy(@RequestBody SlideshowyDTO slideshowyDTO);

    @ApiOperation(value = "轮播图删除")
    @DeleteMapping(value = "slideshowy/del")
    Result<JSONObject> delslideshowy(Integer id);

    @ApiOperation(value = "轮播图修改")
    @PutMapping(value = "slideshowy/save")
    Result<JSONObject> updateslideshowy(@RequestBody SlideshowyDTO slideshowyDTO);

    @ApiOperation(value = "轮播图查询")
    @GetMapping(value = "slideshowy/info")
    Result<List<SlideshowyEntity>> getslideshowy();

}
