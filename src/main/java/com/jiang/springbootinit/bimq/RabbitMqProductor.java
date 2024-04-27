package com.jiang.springbootinit.bimq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class RabbitMqProductor {

    @Resource
    private RabbitTemplate rabbitTemplate;
    /**
     * 发送消息
     * @param exchange
     * @param routingKey
     * @param message
     */
    public void sendMessage(String exchange,String routingKey, String message) {
        rabbitTemplate.convertAndSend(exchange,routingKey,message);
    }
}
