# Springboot中使用缓存

## 无缓存

#### 创建springboot-mybatis工程

按照 **Spring Boot中使用MyBatis**配置工程，完成后设置mapper的log输出级别为debug

```yaml
# 指定某个包名下的方法log级别
logging:
  level:
    com:
      pemila:
        boot:
          cache:
            redis:
              mapper: debug
```

#### 启动并访问

访问 http://127.0.0.1:8080/cache-redis/query/student?no=001，重复多次可见有如下输出

```log
2019-06-17 14:09:42.445 DEBUG : ==>  Preparing: select * from student where s_no=? 
2019-06-17 14:09:42.445 DEBUG : ==> Parameters: 001(String)
2019-06-17 14:09:42.447 DEBUG : <==      Total: 1
2019-06-17 14:09:42.645 DEBUG : ==>  Preparing: select * from student where s_no=? 
2019-06-17 14:09:42.645 DEBUG : ==> Parameters: 001(String)
2019-06-17 14:09:42.648 DEBUG : <==      Total: 1
2019-06-17 14:09:42.828 DEBUG : ==>  Preparing: select * from student where s_no=? 
2019-06-17 14:09:42.829 DEBUG : ==> Parameters: 001(String)
2019-06-17 14:09:42.831 DEBUG : <==      Total: 1
2019-06-17 14:09:43.020 DEBUG : ==>  Preparing: select * from student where s_no=? 
2019-06-17 14:09:43.020 DEBUG : ==> Parameters: 001(String)
2019-06-17 14:09:43.022 DEBUG : <==      Total: 1
```

可见每次查询均访问了数据库

## 使用缓存(Simple Cache)

#### 修改pom.xml

```xml
<!-- 引入如下依赖-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

#### 修改入口类

```java
@SpringBootApplication
@EnableCaching  // 使用注解开启缓存
public class SpringBootCacheRedisAppliation {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootCacheRedisAppliation.class,args);
    }
}
```

### 添加缓存配置

```java
@CacheConfig(cacheNames = "student")
public interface StudentService {
    int add(Student student);

    @CachePut(key = "#p0.no")
    int update(Student student);
    @CacheEvict(key = "#p0",allEntries = true)
    int deleteByNo(String no);
    @Cacheable(key = "#p0")
    Student queryStudentByNo(String no);
}
```

#### 启动并访问

访问 http://127.0.0.1:8080/cache-redis/query/student?no=001，重复多次log记录如下

```
2019-06-17 14:25:42.495 DEBUG : ==>  Preparing: select * from student where s_no=? 
2019-06-17 14:25:42.668 DEBUG : ==> Parameters: 001(String)
2019-06-17 14:25:42.716 DEBUG : <==      Total: 1
```

可见仅访问了一次数据库，后续的请求并没有访问数据库

#### SpringBoot中支持的缓存注解

##### @CacheConfig

> 用于配置当前类中公用的缓存配置，如上述代码将放回对象存储于名为student的缓存对象中

##### @Cacheable

> 使用于查询请求，查询时会根据条件先从缓存中获取，若不存在才发起对数据库的访问

包含如下参数：

- value、cacheNames: 用于指定缓存存储的集合名
- key： 缓存对象存储在Map集合中的key值，非必须，缺省时使用方法的所有参数组合作为key，自定义时需要使用SpEL表达式（比如`@Cacheable(key = "#p0")`）。[自定义规则](https://docs.spring.io/spring/docs/current/spring-framework-reference/integration.html#cache)

- condition：缓存对象的条件，非必需，也需使用SpEL表达式，只有满足表达式条件的内容才会被缓存，比如：`@Cacheable(key = "#p0", condition = "#p0.length() < 3")`，表示只有当第一个参数的长度小于3的时候才会被缓存；

- `unless`：另外一个缓存条件参数，非必需，需使用SpEL表达式。它不同于condition参数的地方在于它的判断时机，该条件是在函数被调用之后才做判断的，所以它可以通过对result进行判断；
- `keyGenerator`：用于指定key生成器，非必需。若需要指定一个自定义的key生成器，我们需要去实现`org.springframework.cache.interceptor.KeyGenerator`接口，并使用该参数来指定；
- `cacheManager`：用于指定使用哪个缓存管理器，非必需。只有当有多个时才需要使用；
- `cacheResolver`：用于指定使用那个缓存解析器，非必需。需通过org.springframework.cache.interceptor.CacheResolver接口来实现自己的缓存解析器，并用该参数指定；

##### @CachePut

> 配置于函数上，能够根据参数定义条件来进行缓存，其缓存的是方法的返回值，它与`@Cacheable`不同的是，它每次都会真实调用函数，所以主要用于数据新增和修改操作上。它的参数与`@Cacheable`类似，具体功能可参考上面对`@Cacheable`参数的解析；

##### @CacheEvict

> 配置于函数上，通常用在删除方法上，用来从缓存中移除相应数据。除了同`@Cacheable`一样的参数之外，它还有下面两个参数：

- `allEntries`：非必需，默认为false。当为true时，会移除所有数据；
- `beforeInvocation`：非必需，默认为false，会在调用方法之后移除数据。当为true时，会在调用方法之前移除数据。

#### springboot的缓存实现

要使用上Spring Boot的缓存功能，还需要提供一个缓存的具体实现。Spring Boot根据下面的顺序去侦测缓存实现：

>  Generic >  JCache (JSR-107) > EhCache 2.x  > Hazelcast > Infinispan > Redis > Guava > Simple

除了按顺序侦测外，我们也可以通过配置属性spring.cache.type来强制指定。

## 使用Redis做缓存实现

#### 修改pom.xml

```xml
<!-- spring-boot redis -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

#### 配置redis

[Spring Boot Redis配置参考](https://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html#%20REDIS)

```yaml
spring:
    redis:
      # Redis数据库索引（默认为0）
      database: 0
      # Redis服务器地址
      host: 192.168.1.185
      # Redis服务器连接端口
      port: 6379
      # 连接超时时间（毫秒）
      timeout: 0ms
      jedis:
        pool:
          # 连接池中的最大空闲连接
          max-idle: 8
          # 连接池中的最小空闲连接
          min-idle: 0
          # 连接池最大连接数(使用负值表示没有限制）
          max-active: 8
          # 连接池最大阻塞等待时间（使用负值表示没有限制）
          max-wait: -1ms
```

#### 创建RedisConfig配置类

```java
@Configuration
public class RedisConfig extends CachingConfigurerSupport {

    /** 自定义缓存Key生成策略*/
    @Override
    @Bean
    public KeyGenerator keyGenerator(){
        return new KeyGenerator() {
            @Override
            public Object generate(Object o, Method method, Object... objects) {
                StringBuilder buffer = new StringBuilder();
                buffer.append(o.getClass().getName());
                buffer.append(method.getName());
                for(Object obj : objects){
                    buffer.append(obj.toString());
                }
                return buffer.toString();
            }
        };
    }

    /** 配置缓存管理器*/
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory){
        return new RedisCacheManager(
                RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory),
                this.getRedisCacheConfigurationWithTtl(10*60),
                this.getRedisCacheConfigurationMap()
        );
    }


    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate template = new StringRedisTemplate(factory);
        // 设置序列化工具
        setSerializer(template);
        template.afterPropertiesSet();
        return template;
    }
｝
```

#### 启动并测试

访问 http://127.0.0.1:8080/cache-redis/query/student?no=001，并重复多次log如下

```
2019-06-17 15:39:10.267 DEBUG : ==>  Preparing: select * from student where s_no=? 
2019-06-17 15:39:10.429 DEBUG : ==> Parameters: 001(String)
2019-06-17 15:39:10.462 DEBUG : <==      Total: 1
```

同时使用redis客户端查看执行命令：

```
> get student::001

"["com.pemila.boot.cache.redis.model.Student",{"no":"001","name":"KKKKK","sex":"M"}]"
```

可见请求参数存储为key, 结果保存在value中。