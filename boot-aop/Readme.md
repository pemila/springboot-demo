# Springboot中AOP的使用

### 引入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<!-- aop依赖 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.16</version>
    <scope>runtime</scope>
</dependency>
<!-- druid数据源驱动 -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-starter</artifactId>
    <version>1.1.10</version>
</dependency>
```

### 自定义注解

自定义一个方法级别的注解 ***@Log*** ,用于标注需要监控的方法：

```java
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
    String value() default "";
}
```

### 创建Log实体及对应数据库表

###### 建表

```sql
CREATE TABLE `sys_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(50) DEFAULT NULL COMMENT '用户名',
  `operation` varchar(50) DEFAULT NULL COMMENT '用户操作',
  `time` bigint(20) DEFAULT NULL COMMENT '时间',
  `method` varchar(200) DEFAULT NULL COMMENT '请求方法',
  `params` varchar(500) DEFAULT NULL COMMENT '请求参数',
  `ip` varchar(64) DEFAULT NULL COMMENT 'IP地址',
  `create_time` bigint(20) DEFAULT NULL COMMENT '请求时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
```

###### 实体类

```java
public class SysLog {
    private int id;
    private String userName;
    private String operation;
    private Long time;
    private String method;
    private String param;
    private String ip;
    private Long createTime;
}
```

### 日志存储

###### Dao

```java
public interface SysLogDao {
    void saveSysLog(SysLog log);
}
```

###### Dao实现

```java
@Repository("sysLogDao")
public class SysLogDaoImpl implements SysLogDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void saveSysLog(SysLog log) {
        String sql = "insert into sys_log (user_name,operation,time,method,params,ip,create_time) values(:userName,:operation,:time,:method,:param,:ip,:createTime)";
        NamedParameterJdbcTemplate  namedTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        namedTemplate.update(sql,new BeanPropertySqlParameterSource(log));
    }
}
```

### 定义切面和切点

```java
@Aspect
public class LogAspect {
    @Autowired
    private SysLogDao sysLogDao;

    /** 定义切入点为 @Log 注解*/
    @Pointcut("@annotation(com.pemila.boot.aop.config.Log)")
    public void pointcut(){}

    /** 切入方式为 环绕*/
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint){
        Object result = null;
        long beginTime = System.currentTimeMillis();
        try {
            // 执行方法
            result = joinPoint.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        // 执行耗时
        long time = System.currentTimeMillis()-beginTime;
        // 保存日志
        saveLog(joinPoint,time);
        return result;
    }
}
```

### 测试

```java
@RestController
public class TestController {

        @Log("执行方法一")
        @GetMapping("/one")
        public void methodOne(String name) { }

        @Log("执行方法二")
        @GetMapping("/two")
        public void methodTwo() throws InterruptedException {
            Thread.sleep(2000);
        }

        @Log("执行方法三")
        @GetMapping("/three")
        public void methodThree(String name, String age) { }
}
```

分别访问 

- <http://localhost:8080/aopDemo/one?name=KangKang>
- <http://localhost:8080/aopDemo/two>
- <http://localhost:8080/aopDemo/three?name=Mike&age=25>