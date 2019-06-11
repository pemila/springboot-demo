package com.pemila.boot.aop.config;

import java.lang.annotation.*;

/**
 * 定义方法级别的注解
 * @author 月在未央
 * @date 2019/6/11 10:17
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
    String value() default "";
}
