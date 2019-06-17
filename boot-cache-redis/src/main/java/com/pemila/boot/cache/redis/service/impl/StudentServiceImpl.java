package com.pemila.boot.cache.redis.service.impl;

import com.pemila.boot.cache.redis.mapper.StudentMapper;
import com.pemila.boot.cache.redis.model.Student;
import com.pemila.boot.cache.redis.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 月在未央
 * @date 2019/6/10 15:38
 */
@Service("StudentService")
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentMapper studentMapper;
    @Override
    public int add(Student student) {
        return studentMapper.add(student);
    }
    @Override
    public int update(Student student) {
        return studentMapper.update(student);
    }
    @Override
    public int deleteByNo(String no) {
        return studentMapper.deleteById(no);
    }
    @Override
    public Student queryStudentByNo(String no) {
        return studentMapper.queryStudentById(no);
    }
}
