package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.base.BaseApiService;
import com.baidu.base.Result;
import com.baidu.shop.dto.UserDTO;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.mapper.UserMapper;
import com.baidu.shop.redis.repository.RedisRepository;
import com.baidu.shop.service.UserService;
import com.baidu.status.HTTPStatus;
import com.baidu.utils.BCryptUtil;
import com.baidu.utils.BaiduBeanUtil;
import com.baidu.utils.LuosimaoDuanxinUtil;
import com.baidu.utils.userConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @ClassName UserServiceImpl
 * @Description: TODO
 * @Author gengzifang
 * @Date 2020/10/13
 * @Version V1.0
 **/
@RestController
@Slf4j
public class UserServiceImpl extends BaseApiService implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisRepository redisRepository;

    @Override
    public Result<JSONObject> register(UserDTO userDTO) {

        UserEntity userEntity = BaiduBeanUtil.copyProperties(userDTO, UserEntity.class);
        userEntity.setPassword(BCryptUtil.hashpw(userEntity.getPassword(), BCryptUtil.gensalt()));
        userEntity.setCreated(new Date());

        userMapper.insertSelective(userEntity);
        return this.setResultSuccess();
    }

    @Override
    public Result<List<UserEntity>> checkUserNameOrPhone(String value, Integer type) {

        Example example = new Example(UserEntity.class);
        Example.Criteria criteria = example.createCriteria();

        if (type == userConstant.USER_TYPE_USERNAME) {
            criteria.andEqualTo("username", value);
        } else if (type == userConstant.USER_TYPE_PHONE) {
            criteria.andEqualTo("phone", value);
        }

        List<UserEntity> userList = userMapper.selectByExample(example);

        return this.setResultSuccess(userList);
    }

    @Override
    public Result<JSONObject> sendValidCode(UserDTO userDTO) {

        //生成随机6位验证码
        String code = (int) ((Math.random() * 9 + 1) * 100000) + "";

        //发送短信验证码
        //LuosimaoDuanxinUtil.SendCode(userDTO.getPhone(), code);

        //短信条数只有10条,不够我们测试.所以就不发送短信验证码了,直接在控制台打印就可以
        log.debug("向手机号码:{} 发送验证码:{}",userDTO.getPhone(),code);


        redisRepository.set(userConstant.USER_PHONE_CODE_PRE + userDTO.getPhone(),code);

        redisRepository.expire(userConstant.USER_PHONE_CODE_PRE + userDTO.getPhone(),120);


        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> checkValidCode(String phone, String code) {

        String s = redisRepository.get(userConstant.USER_PHONE_CODE_PRE + phone);
        if(!code.equals(s)){
            return this.setResultError(HTTPStatus.VALID_CODE_ERROR,"验证码输入错误");
        }

        return this.setResultSuccess();
    }


}
