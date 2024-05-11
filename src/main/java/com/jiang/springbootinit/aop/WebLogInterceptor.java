package com.jiang.springbootinit.aop;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONUtil;
import com.jiang.springbootinit.common.WebLog;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;

@Aspect//切面
@Component
@Slf4j
@Order(1) //默认是最低优先级,值越小优先级越高
public class WebLogInterceptor {

    private static final Logger LOGGER= LoggerFactory.getLogger(WebLogInterceptor.class);


    //切入点：设置规则：
    @Pointcut("execution(public * com.jiang.springbootinit.controller.*.*(..))")
    public void weblog(){

    }

    //通知
    @Before("weblog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable{

    }

    // Object ret:是目标方法执行后的返回值，根据返回值做你的切面的功能处理
    @AfterReturning(value = "weblog()",returning = "ret")
    public void doAfterReturning(Object  ret) throws Throwable{

    }

    @Around("weblog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable{
        long startTime = System.currentTimeMillis();

        //获取当前请求对象
        ServletRequestAttributes attributes  = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        //记录请求信息
        WebLog webLog = new WebLog();
        //在环绕通知中，pjp.proceed() 的作用是触发目标方法的执行。它类似于一个开关，用于控制目标方法是否执行以及何时执行。
        Object result = joinPoint.proceed(); //得到调用函数的返回值：BaseResponse
//        System.out.println("result:" +result);

        //获取被增强的方法相关信息
        Signature signature = joinPoint.getSignature();

        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();

        //大白话：ApiOperation类型的注解是否在调用的类上。
        if (method.isAnnotationPresent(ApiOperation.class)){
            ApiOperation log = method.getAnnotation(ApiOperation.class);
            System.out.println("log"+log);
            webLog.setDescription(log.value());
        }

        long endTime = System.currentTimeMillis();
        webLog.setStartTime(startTime);
        webLog.setSpendTime((int) (endTime -startTime));
        String url = request.getRequestURL().toString(); // http://localhost:8111/api/user/login
        webLog.setUrl(url);
        //移除后缀
        webLog.setBasePath(StrUtil.removeSuffix(url, URLUtil.url(url).getPath()));
        webLog.setIp(request.getRemoteUser()); //设置IP
        webLog.setResult(result);
        HashMap<String, Object> logMap  = new HashMap<>();
        logMap.put("spendTime",webLog.getSpendTime());
        logMap.put("description",webLog.getDescription());
        LOGGER.info("{} " , JSONUtil.parse(webLog));

        return result;
    }



}
