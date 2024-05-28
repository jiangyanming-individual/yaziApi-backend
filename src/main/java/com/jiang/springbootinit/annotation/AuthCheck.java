package com.jiang.springbootinit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验，自定义角色权限注解： 作用在方法上
 */
@Target(ElementType.METHOD) //作用在方法上
@Retention(RetentionPolicy.RUNTIME) // 运行时有效果
public @interface AuthCheck {
    /**
     * 必须有某个角色
     * @return
     */
    String mustRole() default "";

}

