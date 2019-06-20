package com.pemila.boot.autoconfig.annotation;

import com.pemila.boot.autoconfig.config.HelloWorldConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author 月在未央
 * @date 2019/6/20 14:18
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(HelloWorldConfiguration.class)
public @interface EnableHelloWorld {

}
