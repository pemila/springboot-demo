package com.pemila.boot.jdbctemplate.model.domain;

import com.pemila.boot.jdbctemplate.model.Student;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author 月在未央
 * @date 2019/6/10 16:19
 */
public class StudentMapper implements RowMapper<Student> {
    @Override
    public Student mapRow(ResultSet resultSet, int i) throws SQLException {
        Student student = new Student();
        student.setNo(resultSet.getString("s_no"));
        student.setName(resultSet.getString("s_name"));
        student.setSex(resultSet.getString("s_sex"));
        return student;
    }
}
