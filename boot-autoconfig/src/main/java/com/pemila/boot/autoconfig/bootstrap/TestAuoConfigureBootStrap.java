package com.pemila.boot.autoconfig.bootstrap;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author 月在未央
 * @date 2019/6/20 15:33
 */
@EnableAutoConfiguration
public class TestAuoConfigureBootStrap {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(TestEnableBootStrap.class)
                .web(WebApplicationType.NONE)
                .run(args);
        String hello = context.getBean("hello",String.class);
        System.out.println("hello bean : "+ hello);
        context.close();
    }
}
