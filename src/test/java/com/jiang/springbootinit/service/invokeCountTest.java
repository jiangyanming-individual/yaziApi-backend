package com.jiang.springbootinit.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class invokeCountTest {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Test
    public void  test(){
        Boolean b = userInterfaceInfoService.invokeCount(1L, 1L);
        Assertions.assertTrue(b);
    }
}
