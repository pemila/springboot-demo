package com.pemila.boot.jdbctemplate.controller;

import com.pemila.boot.jdbctemplate.model.Student;
import com.pemila.boot.jdbctemplate.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 月在未央
 * @date 2019/6/10 15:41
 */
@RestController
public class StudentController {
    @Autowired
    private StudentService studentService;

    @GetMapping("/query/student")
    public Student queryStudentBySno(String no) {
        return this.studentService.queryStudentByNo(no);
    }
}
