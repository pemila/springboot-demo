package com.pemila.boot.analysis.application.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @author 月在未央
 * @date 2019/6/27 12:09
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HelloApplicationContextInitializeImpl implements ApplicationContextInitializer {
    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        System.out.println("SpringBoot init : HelloApplicationContextInitializeImpl +" + configurableApplicationContext.getId());
    }
}
