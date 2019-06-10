package com.pemila.boot.mybatis.mapper;

import com.pemila.boot.mybatis.model.Student;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

/**
 * @author 月在未央
 * @date 2019/6/10 15:26
 */
@Component
@Mapper
public interface StudentMapper {
    @Insert("insert into student(s_no,s_name,s_sex) values(#{no},#{name},#{sex})")
    int add(Student student);

    @Update("update student set s_name=#{name},s_sex=#{sex} where s_no=#{no}")
    int update(Student student);

    @Delete("delete from student where s_no=#{no}")
    int deleteById(String no);

    @Select("select * from student where s_no=#{no}")
    @Results(id = "student",value= {
            @Result(property = "no", column = "s_no", javaType = String.class),
            @Result(property = "name", column = "s_name", javaType = String.class),
            @Result(property = "sex", column = "s_sex", javaType = String.class)
    })
    Student queryStudentById(String no);
}
