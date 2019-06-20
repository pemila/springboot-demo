package com.pemila.boot.autoconfig.annotation;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * @author 月在未央
 * @date 2019/6/20 11:35
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
public @interface FirstLevelService {
    String value() default "";
}
