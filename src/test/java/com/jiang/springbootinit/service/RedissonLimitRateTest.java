package com.jiang.springbootinit.service;

import com.jiang.springbootinit.manager.RedissonLimitRateManager;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author Lenovo
 * @date 2024/4/21
 * @time 9:55
 * @project yaziApi-backend
 **/
@SpringBootTest
public class RedissonLimitRateTest {


    @Resource
    private RedissonLimitRateManager redissonLimitRateManager;


    @Test
    public void doRateLimitTets() throws InterruptedException {

        String userId="1";

        for (int i = 0; i <2 ; i++) {
            redissonLimitRateManager.doLimitRate(userId);
            System.out.println("成功");
        }
        Thread.sleep(1000);
        for (int i = 0;i< 5; i++){
            redissonLimitRateManager.doLimitRate(userId);
            System.out.println("成功");
        }
    }
}
