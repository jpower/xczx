package com.xuecheng.manager.cms.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Created by 周大侠
 * 2019-04-04 20:17
 */
@Configuration
public class RabbitMqConfig {
    // 队列名称
    @Value("${xuecheng.mq.queue}")
    private String queue_cms_postpage_name;

    // routingkey
    @Value("${xuecheng.mq.routingKey}")
    private  String routingKey;

    // 交换机名称
    @Value("${xuecheng.mq.exchange}")
    private String exchange_cms_name;

    /**
     * 创建交换机
     * @return
     */
    @Bean
    public Exchange EXCHANGE_TOPICS_INFORM() {
        return ExchangeBuilder.directExchange(exchange_cms_name).durable(true).build();
    }

    /**
     * 创建队列
     * @return
     */
    @Bean
    public Queue QUEUE_CMS_POSTPAGE() {
        return new Queue(queue_cms_postpage_name);
    }


    /**
     * 绑定队列和交换机 需要声明routingkey
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding BINDING_QUEUE_EXCHANGE(Queue queue, Exchange exchange) {

        return BindingBuilder.bind(queue).to(exchange).with(routingKey).noargs();
    }

}
