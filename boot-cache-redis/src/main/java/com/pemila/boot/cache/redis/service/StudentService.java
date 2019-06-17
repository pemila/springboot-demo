package com.pemila.boot.cache.redis.service;

import com.pemila.boot.cache.redis.model.Student;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

/**
 * @author 月在未央
 * @date 2019/6/10 15:37
 */
@CacheConfig(cacheNames = "student")
public interface StudentService {
    int add(Student student);

    @CachePut(key = "#p0.no")
    int update(Student student);
    @CacheEvict(key = "#p0",allEntries = true)
    int deleteByNo(String no);
    @Cacheable(key = "#p0")
    Student queryStudentByNo(String no);
}
