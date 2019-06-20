package com.pemila.boot.autoconfig.annotation;

import com.pemila.boot.autoconfig.config.HelloWorldImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author 月在未央
 * @date 2019/6/20 14:45
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(HelloWorldImportSelector.class)
public @interface EnableHelloWorldByImportSelector {
}
