package com.pemila.boot.analysis.application.runner;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author 月在未央
 * @date 2019/6/27 15:41
 */
@Component
public class HelloWorldRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("Runner 执行");
    }
}
