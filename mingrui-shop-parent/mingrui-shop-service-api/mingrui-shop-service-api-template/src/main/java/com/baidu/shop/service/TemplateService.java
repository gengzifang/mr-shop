package com.baidu.shop.service;

import com.baidu.base.Result;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

@Api(tags = "模板接口")
public interface TemplateService {

    @ApiOperation(value = "通过spuId生成静态HTML文件")
    @GetMapping(value = "template/createStaticHTMLTemplate")
    Result<JsonObject> createStaticHTMLTemplate(Integer spuId);

    @ApiOperation(value = "初始化静态HTML文件")
    @GetMapping(value = "template/initStaticHTMLTemplate")
    Result<JsonObject> initStaticHTMLTemplate();
}
