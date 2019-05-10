package com.xuecheng.auth.controller;

import com.xuecheng.api.auth.AuthControllerApi;
import com.xuecheng.auth.service.AuthService;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.domain.ucenter.response.JwtResult;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;

/**
 * Created by 周大侠
 * 2019-04-13 9:43
 */
@RestController
public class AuthController implements AuthControllerApi {

    @Value("${auth.clientId}")
    private String clientId;
    @Value("${auth.clientSecret}")
    private String clientSecret;
    @Value("${auth.cookieDomain}")
    String cookieDomain;
    @Value("${auth.cookieMaxAge}")
    int cookieMaxAge;
    @Autowired
    private AuthService authService;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private HttpServletRequest request;


    @Override
    @PostMapping("/userlogin")
    public LoginResult login(@Valid LoginRequest loginRequest, BindingResult bindingResult) {
        // 校验用户名密码是否为空
        if(bindingResult.hasErrors()) {
            ExceptionCast.exception(bindingResult.getFieldError().getDefaultMessage());
        }

        AuthToken authToken = authService.login(loginRequest.getUsername(), loginRequest.getPassword(), clientId, clientSecret);
        // 将令牌保存至cookie
        String access_token = authToken.getAccess_token();
        CookieUtil.addCookie(response,cookieDomain,"/","uid",access_token,cookieMaxAge,false);
        return new LoginResult(CommonCode.SUCCESS, access_token);

    }

    /**
     * 删除cookie和redis中的令牌
     * @return
     */
    @Override
    @PostMapping("/userlogout")
    public ResponseResult logout() {
        Map<String, String> readCookie = CookieUtil.readCookie(request, "uid");
        String acess_token = readCookie.get("uid");
        // 删除cookie中的令牌
        CookieUtil.addCookie(response,cookieDomain,"/","uid",acess_token,0,false);
        // 删除redis中的令牌
        Boolean state = authService.deleteTokenFormRedis(acess_token);
        if(!state) {
            ExceptionCast.exception("退出失败");
        }

        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 从redis查出令牌返回给前端
     * @return
     */
    @Override
    @GetMapping("/userjwt")
    public JwtResult userJwt() {
        Map<String, String> readCookie = CookieUtil.readCookie(request, "uid");
        String acess_token = readCookie.get("uid");
        AuthToken authToken = authService.getTokenFormRedis(acess_token);
        if(authToken == null) {
            return new JwtResult(CommonCode.FAIL,null);
        }

        return new JwtResult(CommonCode.SUCCESS, authToken.getJwt_token());
    }
}
