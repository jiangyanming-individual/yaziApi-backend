package com.jiang.springbootinit.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 创建redisson 客户端，用于限流的配置
 */

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.redis") //读取配置文件
public class RedissonConfig {


    private String host;
    private String port;
    private Integer database;
    private String passWord;


    @Bean
    public RedissonClient redissonClient(){

        Config config = new Config();
        config.useSingleServer().
                setDatabase(database).
                setAddress("redis://"+host+":"+port);
        //创建实例；
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }

}
