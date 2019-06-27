package com.pemila.boot.analysis.application.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @author 月在未央
 * @date 2019/6/27 14:05
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AfterContextClosedListener implements ApplicationListener<ContextClosedEvent> {
    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        System.out.println("AfterContextClosed : 服务关闭");
    }
}
