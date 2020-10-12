package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.base.Result;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.validate.group.MingruiOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "规格接口")
public interface SpecificationService {

    @ApiOperation(value = "通过条件查询规格组")
    @GetMapping(value = "specgroup/getSpecGroupInfo")
    Result<List<SpecGroupEntity>> getSepcGroupInfo(@SpringQueryMap SpecGroupDTO specGroupDTO);

    @ApiOperation(value = "新增规格组")
    @PostMapping(value = "specgroup/save")
    Result<List<SpecGroupEntity>> save(@Validated({MingruiOperation.Add.class}) @RequestBody SpecGroupDTO specGroupDTO);

    @ApiOperation(value = "修改规格组")
    @PutMapping(value = "specgroup/save")
    Result<List<SpecGroupEntity>> updates(@Validated({MingruiOperation.Add.class}) @RequestBody SpecGroupDTO specGroupDTO);

    @ApiOperation(value = "删除规格组")
    @DeleteMapping(value = "specgroup/delete")
    Result<List<SpecGroupEntity>> deletes(SpecGroupDTO specGroupDTO);

    @ApiOperation(value = "查询规格参数")
    @GetMapping(value = "specparam/getSpecParamInfo")
    Result<List<SpecParamEntity>> getSpecParamInfo(@SpringQueryMap SpecParamDTO specParamDTO);

    @ApiOperation(value = "新增规格参数")
    @PostMapping(value = "specparam/saveorupdate")
    Result<JSONObject> saveSpecparam(@Validated({MingruiOperation.Add.class}) @RequestBody SpecParamDTO specParamDTO);

    @ApiOperation(value = "修改规格参数")
    @PutMapping(value = "specparam/saveorupdate")
    Result<JSONObject> updateSpecparam(@Validated({MingruiOperation.Update.class}) @RequestBody SpecParamDTO specParamDTO);

    @ApiOperation(value = "删除规格参数")
    @DeleteMapping(value = "specparam/delete")
    Result<JSONObject> deleteSpecparam(Integer id);
}
