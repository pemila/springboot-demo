package com.pemila.boot.jdbctemplate.dao.impl;

import com.pemila.boot.jdbctemplate.dao.StudentDao;
import com.pemila.boot.jdbctemplate.model.Student;
import com.pemila.boot.jdbctemplate.model.domain.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.List;

/**
 * @author 月在未央
 * @date 2019/6/10 16:13
 */
@Repository("studentDao")
public class StudentDaoImpl implements StudentDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int add(Student student) {
        String sql = "insert into student(s_no,s_name,s_sex) values(:no,:name,:sex)";
        NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(jdbcTemplate.getDataSource());
        return namedTemplate.update(sql, new BeanPropertySqlParameterSource(student));
    }

    @Override
    public int update(Student student) {
        String sql = "update student set s_name = ?,s_sex = ? where s_no = ?";
        Object[] args = { student.getName(), student.getSex(), student.getNo() };
        int[] argTypes = { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR };
        return jdbcTemplate.update(sql, args, argTypes);
    }

    @Override
    public int deleteById(String no) {
        String sql = "delete from student where s_no = ?";
        Object[] args = { no };
        int[] argTypes = { Types.VARCHAR };
        return jdbcTemplate.update(sql, args, argTypes);
    }

    @Override
    public Student queryStudentById(String no) {
        String sql = "select * from student where s_no = ?";
        Object[] args = { no };
        int[] argTypes = { Types.VARCHAR };
        List<Student> studentList = jdbcTemplate.query(sql, args, argTypes, new StudentMapper());
        if (studentList.size() > 0) {
            return studentList.get(0);
        } else {
            return null;
        }
    }
}
