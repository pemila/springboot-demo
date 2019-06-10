# Spring Boot中使用MyBatis

### mybatis-spring-boot-starter

在pom文件中引入

```xml
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>2.0.1</version>
</dependency>
```

不同版本的springboot和mybatis版本对应不同，具体可查看官方文档：<http://www.mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/>

### Druid数据源

[Druid](https://github.com/alibaba/druid)是一个关系型数据库连接池，durid不仅提供连接池功能，还提供监控功能，可以实时查看数据库连接池和SQL查询的工作情况。

##### 配置druid依赖

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot2-starter</artifactId>
    <version>1.1.10</version>
</dependency>
```

##### Druid数据源配置

springboot的数据元配置默认为*org.apache.tomcat.jdbc.pool.Datasource*，为了使用Druid连接池，需要进行如下配置：

```yaml
server:
  servlet:
    context-path: /web
spring:
  datasource:
    druid:
      # 数据库访问配置, 使用druid数据源
      db-type: mysql
      driver-class-name: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://192.168.1.185:3306/test?useSSL=true&serverTimezone=UTC
      username: dba369
      password: nine123
      # 连接池配置
      initial-size: 5
      min-idle: 5
      max-active: 20
      # 连接等待超时时间
      max-wait: 30000
      # 配置检测可以关闭的空闲连接间隔时间
      time-between-eviction-runs-millis: 60000
      # 配置连接在池中的最小生存时间
      min-evictable-idle-time-millis: 300000
      validation-query: select '1'
      test-while-idle: true
      test-on-borrow: false
      test-on-return: false
      # 打开PSCache，并且指定每个连接上PSCache的大小
      pool-prepared-statements: true
      max-open-prepared-statements: 20
      max-pool-prepared-statement-per-connection-size: 20
      # 配置监控统计拦截的filters, 去掉后监控界面sql无法统计, 'wall'用于防火墙
      filters: stat,wall
      # Spring监控AOP切入点，如x.y.z.service.*,配置多个英文逗号分隔
      aop-patterns: com.pemila.boot.mybatis.servie.*


      # WebStatFilter配置
      web-stat-filter:
        enabled: true
        # 添加过滤规则
        url-pattern: /*
        # 忽略过滤的格式
        exclusions: '*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*'

      # StatViewServlet配置
      stat-view-servlet:
        enabled: true
        # 访问路径为/druid时，跳转到StatViewServlet
        url-pattern: /druid/*
        # 是否能够重置数据
        reset-enable: false
        # 需要账号密码才能访问控制台
        login-username: druid
        login-password: druid123
        # IP白名单
        # allow: 127.0.0.1
        #　IP黑名单（共同存在时，deny优先于allow）
        # deny: 192.168.1.218

      # 配置StatFilter
      filter:
        stat:
          log-slow-sql: true
```

上述配置不但配置了Druid作为连接池，并且开启了Druid的监控功能。其他配置可参考官方wiki<https://github.com/alibaba/druid/tree/master/druid-spring-boot-starter>

此时运行项目，并访问 <http://localhost:8080/web/druid>，输入账号密码即可进入Druid监控后台。



### 使用MyBatis

##### 创建表：

```sql
CREATE TABLE `student` (
  `s_no` varchar(3) NOT NULL,
  `s_name` varchar(9) DEFAULT NULL,
  `s_sex` char(2) DEFAULT NULL,
  PRIMARY KEY (`s_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

"s_no"	"s_name"	"s_sex"
"001"	"KKKKK"			"M"
"002"	"Mike"			"M"
"003"	"Jane"			"F"
```

##### 创建对应实体：

```java
public class Student implements Serializable {
    private String no;
    private String name;
    private String sex;
}
```

##### 创建包含CRUD的StudentMapper:

```java
public interface StudentMapper {
    int add(Student student);
    int update(Student student);
    int deleteById(String no);
    Student queryStudentById(String no);
}
```

##### StudentMapper的实现

Mapper的实现可以基于xml也可以基于注解

###### 注解方式

```java
@Component
@Mapper
public interface StudentMapper {
    @Insert("insert into student(s_no,s_name,s_sex) values(#{no},#{name},#{sex})")
    int add(Student student);

    @Update("update student set s_name=#{name},s_sex=#{sex} where s_no=#{no}")
    int update(Student student);

    @Delete("delete from student where s_no=#{no}")
    int deleteById(String no);

    @Select("select * from student where sno=#{sno}")
    @Results(id = "student",value= {
            @Result(property = "no", column = "s_no", javaType = String.class),
            @Result(property = "name", column = "s_name", javaType = String.class),
            @Result(property = "sex", column = "s_sex", javaType = String.class)
    })
    Student queryStudentById(String no);
}
```

简单语句只需要使用@Insert、@Update、@Delete、@Select这4个注解即可，动态SQL语句需要使用@InsertProvider、@UpdateProvider、@DeleteProvider、@SelectProvider等注解。具体可参考官方文档<http://www.mybatis.org/mybatis-3/zh/java-api.html>。

###### xml方式

使用xml方式需要在application.yml中进行额外的配置

```yaml
mybatis:
  # type-aliases扫描路径
  # type-aliases-package:
  # mapper xml实现扫描路径
  mapper-locations: classpath:mapper/*.xml
  property:
    order: BEFORE
```

### 测试

##### 编写Service

```java
public interface StudentService {
    int add(Student student);
    int update(Student student);
    int deleteByNo(String no);
    Student queryStudentByNo(String no);
}
```

##### 实现类

```java
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
```

##### 编写Controller

``` java
@RestController
public class StudentController {
    @Autowired
    private StudentService studentService;

    @GetMapping("/queryStudent")
    public Student queryStudentBySno(String no) {
        return this.studentService.queryStudentByNo(no);
    }
}
```

此时项目目录如下

```

|-- boot-mybatis
|-- boot-mybatis.iml
|-- pom.xml
|-- src
    |-- main
    |   |-- java
    |   |   |-- com
    |   |       |-- pemila
    |   |           |-- boot
    |   |               |-- mybatis
    |   |                   |-- SpringBootMybatisApplication.java
    |   |                   |-- controller
    |   |                   |   |-- StudentController.java
    |   |                   |-- mapper
    |   |                   |   |-- StudentMapper.java
    |   |                   |-- model
    |   |                   |   |-- Student.java
    |   |                   |-- service
    |   |                       |-- StudentService.java
    |   |                       |-- impl
    |   |                           |-- StudentServiceImpl.java
    |   |-- resources
    |       |-- application.yml
    |-- test
        |-- java

```

##### 启动项目并访问

#####  <http://localhost:8080/web/queryStudent?no=001>

响应结果如下：

```json
{
    "no": "001",
    "name": "KKKKK",
    "sex": "M"
}
```

访问http://localhost:8080/web/druid可查看druid监控获得的sql执行数据