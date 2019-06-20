package com.pemila.boot.autoconfig.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 月在未央
 * @date 2019/6/20 14:16
 */
@Configuration
public class HelloWorldConfiguration {

    @Bean
    public String hello(){
        return "hello world";
    }
}
