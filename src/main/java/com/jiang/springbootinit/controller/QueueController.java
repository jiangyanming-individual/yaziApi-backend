package com.jiang.springbootinit.controller;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Lenovo
 * @date 2024/4/21
 * @time 17:19
 * @project springboot-init
 **/
@RestController
@RequestMapping("/queue")
@Profile({"dev","test"})
@Slf4j
//跨域问题
@CrossOrigin(origins = "http://localhost:8000", allowCredentials = "true")
public class QueueController {

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @GetMapping("/add")
    public void add(String name){
        //实现Runnable 接口
        CompletableFuture.runAsync(()->{
            log.info("任务正在执行："+name+"执行线程："+Thread.currentThread().getName());
            try {
                Thread.sleep(600000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e); //抛出异常
            }
        },threadPoolExecutor);
    }

    @GetMapping("/get")
    public String get(){

        HashMap<String, Object> map = new HashMap<>();
        int size = threadPoolExecutor.getQueue().size();
        map.put("阻塞队列长度",size);
        long taskCount = threadPoolExecutor.getTaskCount();
        map.put("任务总数",taskCount);
        long completedTaskCount = threadPoolExecutor.getCompletedTaskCount();
        map.put("已经完成任务总数",completedTaskCount);
        int activeCount = threadPoolExecutor.getActiveCount();
        map.put("正在工作的线程数",activeCount);
        //转为json：
        return JSONUtil.toJsonStr(map);
    }
}
