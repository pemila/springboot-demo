package com.pemila.boot.autoconfig.config;

import com.pemila.boot.autoconfig.annotation.EnableHelloWorld;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * @author 月在未央
 * @date 2019/6/20 15:22
 */
@Configuration
@EnableHelloWorld
//@ConditionalOnProperty(name = "hello",havingValue = "false")
public class HelloWorldAutoConfiguration {
}
