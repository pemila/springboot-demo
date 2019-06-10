package com.pemila.boot.mybatis.service;

import com.pemila.boot.mybatis.model.Student;

/**
 * @author 月在未央
 * @date 2019/6/10 15:37
 */
public interface StudentService {
    int add(Student student);
    int update(Student student);
    int deleteByNo(String no);
    Student queryStudentByNo(String no);
}
