package com.pemila.boot.autoconfig.bootstrap;

import com.pemila.boot.autoconfig.service.TestService;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * 自定义一个启动类
 * 扫描TestService.class所在包
 * @author 月在未央
 * @date 2019/6/20 11:39
 */
@ComponentScan("com.pemila.boot.autoconfig.service")
public class ServiceBootStrap {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(ServiceBootStrap.class)
                .web(WebApplicationType.NONE)
                .run(args);
        TestService service = context.getBean("testService",TestService.class);
        System.out.println("TestServiceBean: " + service);
        context.close();
    }
}
