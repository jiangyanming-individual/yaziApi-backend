package com.jiang.springbootinit.manager;

import com.jiang.springbootinit.common.ErrorCode;
import com.jiang.springbootinit.exception.ThrowUtils;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author Lenovo
 * @date 2024/4/21
 * @time 9:42
 * @project yaziApi-backend
 **/
@Service
public class RedissonLimitRateManager {


    @Resource
    private RedissonClient redissonClient;

    /**
     * key:用于限制操作的key
     * @param key
     */
    public void doLimitRate(String key){

        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        //每秒限制两次操作：
        //参数1：限流的类型， 参数2 限流的次数， 参数3：限流的时间间隔，参数4：限流的时间单位；
        rateLimiter.trySetRate(RateType.OVERALL,2,1, RateIntervalUnit.SECONDS);
        boolean canOp= rateLimiter.tryAcquire(1);
        ThrowUtils.throwIf(!canOp, ErrorCode.TOO_MANY_REQUEST,"请求过于频繁");

    }
}
