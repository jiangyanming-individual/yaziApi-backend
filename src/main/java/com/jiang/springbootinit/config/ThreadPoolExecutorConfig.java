package com.jiang.springbootinit.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Lenovo
 * @date 2024/4/21
 * @time 17:11
 * @project springboot-init
 **/
@Configuration
public class ThreadPoolExecutorConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
         //新建一个线程工厂
         ThreadFactory threadFactory=new ThreadFactory() {
             int count=1;
             @Override
             public Thread newThread(@NotNull Runnable r) {
                 // 一定要将这个 r 放入到线程当中
                 Thread thread = new Thread(r);
                 thread.setName("线程："+count);
                 count++; //任务数++
                 return thread;
             }
         };
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 4,
                100, TimeUnit.SECONDS, new ArrayBlockingQueue<>(4), threadFactory);
        //返回线程池：
        return threadPoolExecutor;
    }
}
