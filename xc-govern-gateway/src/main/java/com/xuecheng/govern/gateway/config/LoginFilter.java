package com.xuecheng.govern.gateway.config;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by 周大侠
 * 2019-04-13 19:42
 */
@Component
public class LoginFilter extends ZuulFilter {
    @Autowired
    HttpServletRequest request;

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public String filterType() {
        /**
         pre：请求在被路由之前执行

         routing：在路由请求时调用

         post：在routing和errror过滤器之后调用

         error：处理请求时发生错误调用

         */
        return "pre";
    }

    @Override
    public int filterOrder() {
        //过虑器序号，越小越被优先执行
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        //返回true表示要执行此过虑器
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        //过虑器的内容
        Map<String, String> readCookie = CookieUtil.readCookie(request, "uid");
        if(readCookie == null || readCookie.isEmpty()) {
            return access_denied();
        }
        String authorization = request.getHeader("Authorization");
        if(StringUtils.isEmpty(authorization)) {
            return access_denied();
        }
        if(!authorization.startsWith("Bearer ")) {
            return access_denied();
        }
        String uid = readCookie.get("uid");
        Long expire = redisTemplate.getExpire("user_token:" +uid , TimeUnit.SECONDS);
        if(expire <= 0) {
            return access_denied();
        }
        return null;
    }

    /**
     * 拒绝访问
     */
    public Object access_denied() {
        RequestContext currentContext = RequestContext.getCurrentContext();
        // 拒绝访问
        currentContext.setSendZuulResponse(false);
        // 设置响应内容
        ResponseResult responseResult = new ResponseResult(CommonCode.UNAUTHENTICATED);
        String bodey = JSON.toJSONString(responseResult);
        currentContext.setResponseBody(bodey);
        // 设置状态码
        currentContext.setResponseStatusCode(200);
        // 设置相应格式
        HttpServletResponse response = currentContext.getResponse();
        response.setContentType("application/json;charset=utf-8");
        return null;
    }
}
