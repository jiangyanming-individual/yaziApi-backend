package com.jiang.springbootinit.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author Lenovo
 * @date 2024/4/19
 * @time 11:01
 * @project springboot-init
 **/
@SpringBootTest
public class AiManagerTest {

    @Resource
    private AiManager aiManager;

    @Test
    public void test(){
        String doChatResponse = aiManager.doChat(1659171950288818178L,"河南信阳");

        System.out.println(doChatResponse);

    }
}
