package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.base.Result;
import com.baidu.shop.dto.UserDTO;
import com.baidu.shop.entity.UserEntity;
import com.baidu.validate.group.MingruiOperation;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "用户接口")
public interface UserService {

    @ApiOperation(value = "用户注册")
    @PostMapping(value = "user/register")
    Result<JSONObject> register(@Validated({MingruiOperation.Add.class}) @RequestBody UserDTO userDTO);

    @ApiOperation(value = "校验用户名或手机号唯一")
    @GetMapping(value = "user/check/{value}/{type}")
    Result<List<UserEntity>> checkUserNameOrPhone(@PathVariable(value = "value") String value, @PathVariable(value = "type") Integer type);

    @ApiOperation(value = "给手机号发送验证码")
    @PostMapping(value = "user/sendValidCode")
    Result<JSONObject> sendValidCode(@RequestBody UserDTO userDTO);

    @ApiOperation(value = "校验验证码")
    @GetMapping(value = "user/checkValidCode")
    Result<JSONObject> checkValidCode(String phone, String code);

    @ApiOperation(value = "查询用户")
    @GetMapping(value = "user/getuser")
    Result<PageInfo<UserEntity>> getuser(@SpringQueryMap UserDTO userDTO);

    @ApiOperation(value = "校验用户名")
    @GetMapping(value = "user/efficacyUserName")
    Result<List<UserEntity>> efficacyUserName(UserDTO userDTO);

    @ApiOperation(value = "校验手机号")
    @GetMapping(value = "user/efficacyPhone")
    Result<List<UserEntity>> efficacyPhone(UserDTO userDTO);

    @ApiOperation(value = "用户修改")
    @PutMapping(value = "user/register")
    Result<JSONObject> userUpdate(@Validated({MingruiOperation.Update.class}) @RequestBody UserDTO userDTO);

    @ApiOperation(value = "用户是否启用")
    @PutMapping (value = "user/delete")
    Result<JSONObject> userDelete(@Validated({MingruiOperation.Update.class}) @RequestBody UserDTO userDTO);
}

