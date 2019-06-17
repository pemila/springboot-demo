package com.pemila.boot.cache.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @author 月在未央
 * @date 2019/6/11 15:32
 */
@SpringBootApplication
@EnableCaching
public class SpringBootCacheRedisAppliation {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootCacheRedisAppliation.class,args);
    }
}

