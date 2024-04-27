package com.jiang.springbootinit.bimq;

import com.jiang.springbootinit.constant.BiMqConstant;
import com.jiang.springbootinit.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class BiMqProductor {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 生产者
     * @param message
     */
    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(BiMqConstant.BI_EXCHANGE,BiMqConstant.BI_ROUTING_KEY,message);
    }
}
