package com.pemila.boot.jdbctemplate.dao;

import com.pemila.boot.jdbctemplate.model.Student;

/**
 * @author 月在未央
 * @date 2019/6/10 15:26
 */
public interface StudentDao {
    int add(Student student);
    int update(Student student);
    int deleteById(String no);
    Student queryStudentById(String no);
}
