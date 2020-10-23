package com.baidu.shop.feign;

import com.baidu.base.Result;
import com.baidu.shop.business.OrderService;
import com.baidu.shop.dto.OrderInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @ClassName OrderFeign
 * @Description: TODO
 * @Author gengzifang
 * @Date 2020/10/22
 * @Version V1.0
 **/
@FeignClient(value = "order-server" ,contextId = "OrderService")
public interface OrderFeign {

    @PostMapping(value = "order/getOrderInfoByOrderId")
    Result<OrderInfo> getOrderInfoByOrderId(@RequestParam Long orderId);

}
