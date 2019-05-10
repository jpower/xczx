package com.xuecheng.auth.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.exception.ExceptionCast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * Created by 周大侠
 * 2019-04-13 10:11
 */
@Service
public class AuthService {
    @Value("${auth.tokenValiditySeconds}")
    int tokenValiditySeconds;
    @Autowired
    private LoadBalancerClient loadBalancerClient;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 登录操作需获取令牌并保存
     * @param username
     * @param password
     * @param clientId
     * @param clientSecret
     */
    public AuthToken login(String username, String password, String clientId, String clientSecret) {
        // 申请令牌
        AuthToken authToken = applyToken(username, password, clientId, clientSecret);
        if(authToken == null) {
            ExceptionCast.exception(AuthCode.AUTH_EMPTY);
        }
        //用户身份令牌
        String access_token = authToken.getAccess_token();
        //存储到redis中的内容
        String jsonString = JSON.toJSONString(authToken);
        //将令牌存储到redis
        boolean result = this.saveToken(access_token, jsonString, tokenValiditySeconds);
        if (!result) {
            ExceptionCast.exception(AuthCode.AUTH_LOGIN_TOKEN_SAVEFAIL);
        }
        return authToken;


    }

    /**
     * 保存令牌信息进redis
     * @param access_token
     * @param content
     * @param ttl
     * @return
     */
    private boolean saveToken(String access_token,String content,long ttl){
        String key = "user_token:" + access_token;
        redisTemplate.boundValueOps(key).set(content,ttl, TimeUnit.SECONDS);
        Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        return expire > 0;
    }

    /**
     * 获取令牌
     * @param username
     * @param password
     * @param clientId
     * @param clientSecret
     * @return
     */
    private AuthToken applyToken(String username, String password, String clientId, String clientSecret) {
        // 获取认证服务uri
        ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        URI uri = serviceInstance.getUri();
        String url = uri + "/auth/oauth/token";
        // 定义Body
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("username", username);
        body.add("password", password);
        body.add("grant_type", "password");
        // 定义heard
        LinkedMultiValueMap<String, String> heard = new LinkedMultiValueMap<>();
        heard.add("Authorization", getHttpBasic(clientId, clientSecret));

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(body, heard);
        //设置restTemplate远程调用时候，对400和401不让报错，正确返回数据
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if(response.getRawStatusCode()!=400 && response.getRawStatusCode()!=401){
                    super.handleError(response);
                }
            }
        });
        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);
        Map bodyMap = responseEntity.getBody();
        //申请令牌信息
        if(bodyMap == null ||
                bodyMap.get("access_token") == null ||
                bodyMap.get("refresh_token") == null ||
                bodyMap.get("jti") == null){
            //解析spring security返回的错误信息
            if(bodyMap!=null && bodyMap.get("error_description")!=null){
                String errorDescription = (String) bodyMap.get("error_description");
                if(errorDescription.indexOf("Cannot pass null or empty") >= 0){
                    ExceptionCast.exception(AuthCode.AUTH_ACCOUNT_NOTEXISTS);
                }else if(errorDescription.indexOf("坏的凭证") >= 0){
                    ExceptionCast.exception(AuthCode.AUTH_CREDENTIAL_ERROR);
                }
            }

            return null;

        }
        AuthToken authToken = new AuthToken();
        authToken.setAccess_token((String) bodyMap.get("jti"));//用户身份令牌
        authToken.setRefresh_token((String) bodyMap.get("refresh_token"));//刷新令牌
        authToken.setJwt_token((String) bodyMap.get("access_token"));//jwt令牌
        return authToken;
    }

    //获取httpbasic的串
    private String getHttpBasic(String clientId, String clientSecret) {
        String string = clientId + ":" + clientSecret;
        //将串进行base64编码
        byte[] encode = Base64Utils.encode(string.getBytes());
        return "Basic " + new String(encode);
    }

    /**
     * 从redis中取出令牌
     * @param acess_token
     */
    public AuthToken getTokenFormRedis(String acess_token) {
        String token = (String) redisTemplate.boundValueOps("user_token:" + acess_token).get();
        AuthToken authToken = null;
        try {
            authToken = JSON.parseObject(token, AuthToken.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return authToken;
    }

    public Boolean deleteTokenFormRedis(String acess_token) {
        Boolean state = redisTemplate.delete("user_token:" + acess_token);
        return state;
    }
}
