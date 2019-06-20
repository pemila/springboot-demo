### 模式注解

Stereotype Annotation俗称为模式注解，Spring中常见的模式注解有**@Service,@Repository,@Controller**等，均派生自**@Component**注解。

凡是被**@Component**标注的类都会被Spring扫描并纳入IOC容器，包括其派生的注解所标注的类。

##### @Component的 派生和层次

###### 自定义注解

```java
// 使用@Service注解修饰
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
public @interface FirstLevelService {
    String value() default "";
}
```

查看**@Service**源码可见 被 @Component修饰

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Service {
    @AliasFor(
        annotation = Component.class
    )
    String value() default "";
}
```

此时注解的层次如下：

```
|__ @Component
	|__ @Service
		|__ @FirstLevelService
```

###### 定义服务

```java
// 使用自定义的注解进行修饰
// 用于测试当前服务是否会被加入到IOC容器
@FirstLevelService
public class TestService {
}
```

###### 自定义启动类

```java
// 扫描TestService.class所在包
@ComponentScan("com.pemila.boot.autoconfig.service")
public class ServiceBootStrap {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(ServiceBootStrap.class)
                .web(WebApplicationType.NONE)
                .run(args);
        TestService service = context.getBean("testService",TestService.class);
        System.out.println("TestServiceBean: " + service);
        context.close();
    }
}
```

执行后输出内容如下：

```
TestServiceBean: com.pemila.boot.autoconfig.service.TestService@1a45193b
```

可见被**@FirstLevelService**注解标注的TestService.class被加入到了IOC容器中。

### @Enable模块驱动

@Enable模块驱动在Spring Framework 3.1后开始支持，一个模块就是一个为了实现某个功能的组件的集合。通过@Enable模块驱动可以开启响应的功能。

@Enable模块驱动可以分为注解驱动和接口编程两种实现方式。

#### 注解驱动

Spring中，基于注解驱动的实例可以查看 @EnableWebMvc源码：

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({DelegatingWebMvcConfiguration.class})
public @interface EnableWebMvc {
}
```

通过**@Import**导入了配置类 *DelegatingWebMvcConfiguration.class*：

```java
@Configuration
public class DelegatingWebMvcConfiguration extends WebMvcConfigurationSupport {
    private final WebMvcConfigurerComposite configurers = new WebMvcConfigurerComposite();

    public DelegatingWebMvcConfiguration() {
    }
	...
}
```

该配置类又继承自`WebMvcConfigurationSupport`，里面定义了一些Bean的声明。

如上，基于注解驱动的@Enable模块其实是通过@Import注解导入相应的配置类，以实现相应模块的组件注册。当这些组件注册到IOC容器时，模块对应的功能也就可以使用了。

##### 自定义一个基于注解驱动的@Enable模块驱动

###### 定义配置类

```java
// 当前配置类中顶一个名为hello的bean，内容为hello world
@Configuration
public class HelloWorldConfiguration {
    @Bean
    public String hello(){
        return "hello world";
    }
}
```

###### 创建注解

``` java
// 使用 @Import 导入配置类
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(HelloWorldConfiguration.class)
public @interface EnableHelloWorld {
}
```

###### 定义启动类

```java
@EnableHelloWorld
public class TestEnableBootStrap {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(TestEnableBootStrap.class)
                .web(WebApplicationType.NONE)
                .run(args);
        String hello = context.getBean("hello",String.class);
        System.out.println("hello bean : "+ hello);
        context.close();
    }
}
```

运行main方法后控制台输出

```
hello bean : hello world
```

说明自定义的 @EnableHelloWorld 注解驱动成功

#### 接口编程

基于接口编程实现的模块驱动需要使用ImportSelector.class接口

Spring中基于接口编程方式的有**@EnableCaching**注解：

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({CachingConfigurationSelector.class})
public @interface EnableCaching {
    boolean proxyTargetClass() default false;

    AdviceMode mode() default AdviceMode.PROXY;

    int order() default 2147483647;
}
```

从CachingConfigurationSelector.class 源码中可见 CachingConfigurationSelector间接实现了 ImprotSelector接口

```java
public interface ImportSelector {
    String[] selectImports(AnnotationMetadata var1);
}
```

因此接口编程实现@Enable模块驱动的本质是通过@Import导入接口ImportSelector的实现类，该实现类可以定义需要注册到IOC容器中的组件，以此实现响应模块对应组件的注册。

##### 自定义一个使用接口编程实现的@Enable模块驱动

###### 创建ImportSelector的实现类

```java
public class HelloWorldImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[]{HelloWorldConfiguration.class.getName()};
    }
}
```

###### 定义注解

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(HelloWorldImportSelector.class)
public @interface EnableHelloWorldByImportSelector {
}
```

启动类测试

```java
@EnableHelloWorldByImportSelector
public class TestEnableBootStrap {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(TestEnableBootStrap.class)
                .web(WebApplicationType.NONE)
                .run(args);
        String hello = context.getBean("hello",String.class);
        System.out.println("hello bean : "+ hello);
        context.close();
    }
}
```

输出结果为 hello bean : hello world

### 自动装配

SpringBoot中的自动装配技术底层主要用到了下面这些技术：

- Spring 模式注解装配
- Spring @Enable模块装配
- Spring 条件装配
- Spring 工厂加载机制

spring工厂加载机制的实现类为 SpringFactoriesLoader,源码如下：

```java
public final class SpringFactoriesLoader {
    public static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring.factories";
    private static final Log logger = LogFactory.getLog(SpringFactoriesLoader.class);
    private static final Map<ClassLoader, MultiValueMap<String, String>> cache = new ConcurrentReferenceHashMap();

    private SpringFactoriesLoader() {
    }
```

可见其会读取META-INF目录下的spring.factories配置文件。

查看 spring-boot-autoconfigure-2.1.0.RELEASE.jar包下 META-INF/spring.factories文件

```java
# Initializers
org.springframework.context.ApplicationContextInitializer=\
org.springframework.boot.autoconfigure.SharedMetadataReaderFactoryContextInitializer,\
org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportLoggingListener

# Application Listeners
org.springframework.context.ApplicationListener=\
org.springframework.boot.autoconfigure.BackgroundPreinitializer

# Auto Configuration Import Listeners
org.springframework.boot.autoconfigure.AutoConfigurationImportListener=\
org.springframework.boot.autoconfigure.condition.ConditionEvaluationReportAutoConfigurationImportListener

# Auto Configuration Import Filters
org.springframework.boot.autoconfigure.AutoConfigurationImportFilter=\
org.springframework.boot.autoconfigure.condition.OnBeanCondition,\
org.springframework.boot.autoconfigure.condition.OnClassCondition,\
org.springframework.boot.autoconfigure.condition.OnWebApplicationCondition

# Auto Configure
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration,\
org.springframework.boot.autoconfigure.aop.AopAutoConfiguration,\
org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration,\
...
```

当启动类被@EnableAutoConfiguration标注后，上述所有类都会被Spring扫描，看是否可以纳入IOC容器进行管理。

查看 *org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration* 源码：

```java
@Configuration  // 模式注解 
@ConditionalOnClass({RedisOperations.class}) // 条件装配
@EnableConfigurationProperties({RedisProperties.class})  // 模块装配技术
@Import({LettuceConnectionConfiguration.class, JedisConnectionConfiguration.class})
public class RedisAutoConfiguration {
    public RedisAutoConfiguration() {
    }

    @Bean
    @ConditionalOnMissingBean(
        name = {"redisTemplate"}
    )
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) throws UnknownHostException {
        RedisTemplate<Object, Object> template = new RedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean
    @ConditionalOnMissingBean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) throws UnknownHostException {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}
```

按照以上思路可以自定义实现自动装配

##### 自定义实现自动装配

###### 创建autoConfigure类

```java
@Configuration
@EnableHelloWorld
@ConditionalOnProperty(name = "hello",havingValue = "true")
public class HelloWorldAutoConfiguration {
}
```

###### spring.factories配置

```
# auto configure
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  com.pemila.boot.autoconfig.config.HelloWorldAutoConfiguration
```

###### 启动类测试

```java
@EnableAutoConfiguration
public class TestAuoConfigureBootStrap {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(TestEnableBootStrap.class)
                .web(WebApplicationType.NONE)
                .run(args);
        String hello = context.getBean("hello",String.class);
        System.out.println("hello bean : "+ hello);
        context.close();
    }
}
```

输出hello bean : hello world，可见自动装配成功