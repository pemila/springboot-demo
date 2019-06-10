# springboot中使用jdbcTemplate

jdbcTemplate封装了许多SQL操作，方便使用，具体可查看官方文档：<https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/jdbc/core/JdbcTemplate.html>

### 引入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
```

### 配置数据库驱动及数据源

类似 boot-mybatis中的操作

### 代码编写

Dao层使用JdbcTemplate的实现类

```java
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
```

引入*spring-boot-starter-jdbc* 依赖后，可直接在类中注入jdbcTemplate。

返回结果，可以直接使用List<Map<String,Object>>接收，也可以实现*org.springframework.jdbc.core.RowMapper* 接口，将实体对象属性和表字段对应

``` java
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
```

