package com.baidu.shop.feign;

import com.baidu.shop.service.GoodsService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName GoodsFeign
 * @Description: TODO
 * @Author gengzifang
 * @Date 2020/10/20
 * @Version V1.0
 **/
@FeignClient(contextId = "GoodsService",value = "xxx-service")
public interface GoodsFeign extends GoodsService {
}
