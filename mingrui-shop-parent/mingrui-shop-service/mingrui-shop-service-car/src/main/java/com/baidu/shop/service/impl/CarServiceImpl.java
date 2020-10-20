package com.baidu.shop.service.impl;

import com.baidu.base.BaseApiService;
import com.baidu.base.Result;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.dto.Car;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.SkuEntity;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.redis.repository.RedisRepository;
import com.baidu.shop.service.CarService;
import com.baidu.shop.utils.JwtUtils;
import com.baidu.status.MRshopConstant;
import com.baidu.utils.JSONUtil;
import com.baidu.utils.ObjectUtil;
import com.baidu.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName CarServiceImpl
 * @Description: TODO
 * @Author gengzifang
 * @Date 2020/10/19
 * @Version V1.0
 **/
@RestController
@Slf4j
public class CarServiceImpl extends BaseApiService implements CarService {

    @Autowired
    private RedisRepository redisRepository;

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private JwtConfig jwtConfig;


    @Override
    public Result<JSONObject> carNumUpdate(Long skuId, Integer type, String Token) {

        //获取当前登录用户
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(Token, jwtConfig.getPublicKey());

            Car car = redisRepository.getHash(MRshopConstant.USER_GOODS_CAR_PRE + userInfo.getId(), skuId + "", Car.class);


                /*if (type == 1) {
                    car.setNum(car.getNum() + 1);
                } else if (type == 2) {
                    car.setNum(car.getNum() - 1);
                }*/

            //type等于1 就 增加 等于2 就减少


            car.setNum(type == MRshopConstant.CAR_OPERATION_INCREMENT ? car.getNum() + 1 : car.getNum() - 1);


            redisRepository.setHash(MRshopConstant.USER_GOODS_CAR_PRE + userInfo.getId(), car.getSkuId() + "", JSONUtil.toJsonString(car));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> addCar(Car car, String Token) {

        try {
            //通过公钥获取token中的用户信息
            UserInfo userInfo = JwtUtils.getInfoFromToken(Token, jwtConfig.getPublicKey());

            //从redis中获取用户的购物车数据
            Car redisCar = redisRepository.getHash(MRshopConstant.USER_GOODS_CAR_PRE + userInfo.getId(), car.getSkuId() + "", Car.class);

            Car saveCar = null;

            log.debug("通过key : {} ,skuid : {} 获取到的数据为 : {}", MRshopConstant.USER_GOODS_CAR_PRE + userInfo.getId(), car.getSkuId(), redisCar);

            //原来的用户购物车中有当前要添加到购物车中的商品
            if (ObjectUtil.isNotNull(redisCar)) {
                //将原有的数量 加上 新增的数量
                redisCar.setNum(car.getNum() + redisCar.getNum());

                //saveCar 接值
                saveCar = redisCar;

                log.debug("当前用户购物车中有将要新增的商品，重新设置num : {}", redisCar.getNum());

            } else {//当前的用户购物车中没有将要新增的商品信息

                //通过skuid查询sku信息
                Result<SkuEntity> skuResult = goodsFeign.getSkuBySkuId(car.getSkuId());

                if (skuResult.getCode() == 200) {

                    //查询成功给car赋值
                    SkuEntity skuEntity = skuResult.getData();

                    car.setTitle(skuEntity.getTitle());

                    //如果图片不为空就分割取第一个  否则 为空字符串
                    car.setImage(StringUtil.isNotEmpty(skuEntity.getImages()) ? skuEntity.getImages().split(",")[0] : "");

                    //key:id value:规格参数值
                    Map<String, Object> stringObjectMap = JSONUtil.toMap(skuEntity.getOwnSpec());

                    car.setOwnSpec(skuEntity.getOwnSpec());
                    car.setPrice(Long.valueOf(skuEntity.getPrice()));
                    car.setUserId(userInfo.getId());

                    //saveCar 接值
                    saveCar = car;

                    log.debug("新增商品到购物车redis,KEY : {} , skuId : {} , car : {}", MRshopConstant.USER_GOODS_CAR_PRE + userInfo.getId(), car.getSkuId(), JSONUtil.toJsonString(car));
                }

            }
            redisRepository.setHash(MRshopConstant.USER_GOODS_CAR_PRE + userInfo.getId(), car.getSkuId() + "", JSONUtil.toJsonString(saveCar));

            log.debug("新增到redis数据成功");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> mergeCar(String clientCarList, String Token) {

        //将json字符串转换成json对象
        com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(clientCarList);

        //将json对象属性为clientCarList的数据取出来,并且转换成list集合
        List<Car> carList1 = com.alibaba.fastjson.JSONObject.parseArray(jsonObject.get("clientCarList").toString(), Car.class);

        //遍历新增到购物车
        carList1.stream().forEach(car -> {
            this.addCar(car, Token);
        });

        return this.setResultSuccess();
    }

    @Override
    public Result<List<Car>> getUserGoodsCar(String Token) {

        try {
            List<Car> cars = new ArrayList<>();

            //获取当前登录用户
            UserInfo userInfo = JwtUtils.getInfoFromToken(Token, jwtConfig.getPublicKey());

            //通过用户id从redis获取购物车数据
            Map<String, String> goodsCarMap = redisRepository.getHash(MRshopConstant.USER_GOODS_CAR_PRE + userInfo.getId());

            goodsCarMap.forEach((key, value) -> {
                Car car = JSONUtil.toBean(value, Car.class);
                cars.add(car);
            });
            return this.setResultSuccess(cars);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.setResultError("内部错误");
    }


}
