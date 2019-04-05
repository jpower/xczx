package com.xuecheng.manager.cms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Created by 周大侠
 * 2019-03-31 16:53
 */
@SpringBootApplication
@EntityScan(basePackages = "com.xuecheng.framework.domain.cms")
@ComponentScan(basePackages = "com.xuecheng.api")
@ComponentScan(basePackages = "com.xuecheng.manager.cms")
@ComponentScan(basePackages = "com.xuecheng.framework")
public class ManagerCmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ManagerCmsApplication.class);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(new OkHttp3ClientHttpRequestFactory());
    }

}
