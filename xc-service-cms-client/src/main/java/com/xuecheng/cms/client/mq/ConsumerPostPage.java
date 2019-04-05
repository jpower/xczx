package com.xuecheng.cms.client.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.cms.client.service.PageService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * 监听mq发布页面消息
 * Created by 周大侠
 * 2019-04-05 9:34
 */
@Component
public class ConsumerPostPage {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private PageService pageService;

    @RabbitListener(queues = "${xuecheng.mq.queue}")
    public void publishPage(String msg) throws IOException {
        System.out.println(msg);
        Map map = JSON.parseObject(msg, Map.class);
        String pageId = (String) map.get("pageId");

        // 调用发布页面服务
        pageService.publishPage(pageId);
    }

}
