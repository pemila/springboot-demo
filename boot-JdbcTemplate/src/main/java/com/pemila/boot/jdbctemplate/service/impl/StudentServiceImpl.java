package com.pemila.boot.jdbctemplate.service.impl;

import com.pemila.boot.jdbctemplate.dao.StudentDao;
import com.pemila.boot.jdbctemplate.model.Student;
import com.pemila.boot.jdbctemplate.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 月在未央
 * @date 2019/6/10 15:38
 */
@Service("StudentService")
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentDao studentDao;
    @Override
    public int add(Student student) {
        return studentDao.add(student);
    }
    @Override
    public int update(Student student) {
        return studentDao.update(student);
    }
    @Override
    public int deleteByNo(String no) {
        return studentDao.deleteById(no);
    }
    @Override
    public Student queryStudentByNo(String no) {
        return studentDao.queryStudentById(no);
    }
}
