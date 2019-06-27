package com.pemila.boot.analysis.application.listener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author 月在未央
 * @date 2019/6/27 14:42
 */
public class HelloApplicationListener implements SpringApplicationRunListener {

    public HelloApplicationListener(SpringApplication application,String[] args){
        System.out.println("HelloApplicationListener加载");
    }

    @Override
    public void starting() {
        System.out.println("HelloApplicationRunListener starting......");
    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
        System.out.println("environment 准备完毕");
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        System.out.println("context 准备完毕");
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        System.out.println("context 加载完毕");
    }

    @Override
    public void started(ConfigurableApplicationContext context) {
        System.out.println("context 已启动，spring Bean初始化完成");
    }

    @Override
    public void running(ConfigurableApplicationContext context) {
        System.out.println("spring应用运行中");
    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        System.out.println("spring应用运行异常");
    }
}
