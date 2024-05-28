package com.jiang.springbootinit.provider;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * 服务提供接口
 */
@DubboService
public class DemoServiceImpl implements DemoService {

    @Override
    public String sayHello(String name) {
            return "Hello " + name;
    }

    @Override
    public String sayHello2(String name) {
            return name;
    }

}
