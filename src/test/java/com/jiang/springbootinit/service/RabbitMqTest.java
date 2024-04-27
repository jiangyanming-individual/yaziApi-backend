package com.jiang.springbootinit.service;


import com.jiang.springbootinit.bimq.RabbitMqProductor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class RabbitMqTest {

    @Resource
    private RabbitMqProductor rabbitMqProductor;

    @Test
    void test(){
        rabbitMqProductor.sendMessage("code_exchange","code_routingKey","你好,欢迎来到BI系统");
    }
}
