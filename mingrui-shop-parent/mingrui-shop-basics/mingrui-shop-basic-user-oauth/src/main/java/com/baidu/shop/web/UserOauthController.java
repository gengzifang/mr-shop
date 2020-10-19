package com.baidu.shop.web;

import com.baidu.base.BaseApiService;
import com.baidu.base.Result;
import com.baidu.shop.business.UserOauthService;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.utils.CookieUtils;
import com.baidu.shop.utils.JwtUtils;
import com.baidu.status.HTTPStatus;
import com.baidu.utils.ObjectUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName UserOauthController
 * @Description: TODO
 * @Author gengzifang
 * @Date 2020/10/15
 * @Version V1.0
 **/
@RestController
@Api(value = "用户认证接口")
public class UserOauthController extends BaseApiService{

    @Autowired
    private UserOauthService userOauthService;

    @Autowired
    private JwtConfig jwtConfig;

    @PostMapping(value = "ouath/login")
    @ApiOperation(value = "用户登录")
    public Result<JSONObject> login(@RequestBody UserEntity userEntity
            , HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){


        //返回登陆成功或者失败  通过账户去查询用户信息-->对比密码-->createToken
        String Token = userOauthService.login(userEntity,jwtConfig);

        //判断token是否为null
        //为null密码错误
        //将token放入cookie中
        if(ObjectUtil.isNull(Token)){
            return this.setResultError(HTTPStatus.VALID_USER_PASSWORD_ERROR,"用户名或密码错误");
        }

        CookieUtils.setCookie(httpServletRequest,httpServletResponse,jwtConfig.getCookieName(),Token,jwtConfig.getCookieMaxAge(),true);

        return this.setResultSuccess();
    }

    @GetMapping(value = "oauth/verify")
    public Result<UserInfo> verifyUser(@CookieValue(value = "MRSHOP_TOKEN") String token
            ,HttpServletRequest request , HttpServletResponse response){

        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());

            //可以解析token的证明用户是正确的登陆状态,重新生成token 这样登陆状态  又刷新了30分钟
            String newToken = JwtUtils.generateToken(userInfo,jwtConfig.getPrivateKey(), jwtConfig.getExpire());

            //将新的token写入 cookie 过期时间延长
            CookieUtils.setCookie(request,response,jwtConfig.getCookieName(),newToken,jwtConfig.getCookieMaxAge(),true);

            return this.setResultSuccess(userInfo);
        } catch (Exception e) {
            //e.printStackTrace();
            //应该新建http状态为用户验证失败,状态码为403
            return this.setResultError(HTTPStatus.VERIFY_ERROR,"");
        }



    }

}
