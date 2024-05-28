package com.jiang.springbootinit.provider;

import java.util.concurrent.CompletableFuture;

/**
 * 服务接口
 */
public interface DemoService {
    String sayHello(String name);

    String sayHello2(String name);

    //Returns a new CompletableFuture that is already completed with the given value.
    default CompletableFuture<String> sayHelloAsync(String name) {
        return CompletableFuture.completedFuture(sayHello(name));


//        CompletableFuture.supplyAsync() 有返回值的异步调用;
//         CompletableFuture.runAsync() 没有返回值的异步调用;
        
    }
}
