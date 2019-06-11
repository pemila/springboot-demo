package com.pemila.boot.aop.controller;

import com.pemila.boot.aop.config.Log;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 月在未央
 * @date 2019/6/11 13:01
 */
@RestController
public class TestController {

        @Log("执行方法一")
        @GetMapping("/one")
        public void methodOne(String name) { }

        @Log("执行方法二")
        @GetMapping("/two")
        public void methodTwo() throws InterruptedException {
            Thread.sleep(2000);
        }

        @Log("执行方法三")
        @GetMapping("/three")
        public void methodThree(String name, String age) { }
}
