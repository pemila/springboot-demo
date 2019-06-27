# SpringBootApplication启动应用

在Spring boot入口类中，通常调用SpringApplication的run方法启动应用。

### 自定义SpringApplication

##### 使用SpringApplication API

修改入口类代码：

```java
@SpringBootApplication
public class AnalysisSpringApplication {
    public static void main(String[] args) {
//        SpringApplication.run(AnalysisSpringApplication.class,args);
        SpringApplication application = new SpringApplication(AnalysisSpringApplication.class);
        // 关闭启动banner
        application.setBannerMode(Banner.Mode.OFF);
        // 设定应用环境为非web
        application.setWebApplicationType(WebApplicationType.NONE);
        // profiles指定为dev
        application.setAdditionalProfiles("dev");
        // 启动应用
        application.run(args);
    }
}
```

除此之外SpringApplication还包含其他设置项，具体可见源码

```
deduceMainApplicationClass
run
prepareEnvironment
deduceEnvironmentClass
prepareContext
refreshContext
configureHeadlessProperty
getRunListeners
getSpringFactoriesInstances
getSpringFactoriesInstances
createSpringFactoriesInstances
...
```

##### 使用SpringApplicationBuilder API

```java
@SpringBootApplication
public class AnalysisSpringApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(AnalysisSpringApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .web(WebApplicationType.NONE)
                .profiles("dev")
                .run(args);
    }
}
```

### SpringApplication准备阶段

查看SpringApplication构造方法可以将springApplication的启动分为准备阶段和运行阶段

```java
    public SpringApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
        ...
        this.resourceLoader = resourceLoader;
        Assert.notNull(primarySources, "PrimarySources must not be null");
        this.primarySources = new LinkedHashSet(Arrays.asList(primarySources));
        this.webApplicationType = WebApplicationType.deduceFromClasspath();
        this.setInitializers(this.getSpringFactoriesInstances(ApplicationContextInitializer.class));
        this.setListeners(this.getSpringFactoriesInstances(ApplicationListener.class));
        this.mainApplicationClass = this.deduceMainApplicationClass();
    }
```

准备阶段有如下步骤

##### 配置源

> this.primarySources = new LinkedHashSet(Arrays.asList(primarySources));

如上源码用于加载配置的spring boot bean源，即被**@SpringBootApplication**注解修饰的类

通常使用 SpringApplication 或者 SpringApplicationBuilder的构造器直接指定。

```java
@SpringBootApplication
public class AnalysisSpringApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnalysisSpringApplication.class,args);
    }
}
```

其中 **AnalysisSpringApplication.class** 即为spring boot bean源

上述代码也可以修改为如下方式

```java

public class AnalysisSpringApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ApplicationResource.class);
        application.run(args);
    }
    
    @SpringBootApplication
    public static class ApplicationResource(){
	
	}
}
```

从源码 ***this.primarySources = new LinkedHashSet(Arrays.asList(primarySources));***可知除了配置单个源外配置多个源也是可行的。

##### 推断应用类型

> this.webApplicationType = WebApplicationType.deduceFromClasspath();

 WebApplicationType.deduceFromClasspath()用于推断当前应用的类型。

Springboot2.x中应用类型如下：

```java
public enum WebApplicationType {
    NONE,    // 非WEB类型应用
    SERVLET, // WEB servlet应用
    REACTIVE;// WEB reactive应用
    ...
 }
```

方法 **deduceFromClasspath()**

```java
    static WebApplicationType deduceFromClasspath() {
        if (ClassUtils.isPresent("org.springframework.web.reactive.DispatcherHandler", (ClassLoader)null) && !ClassUtils.isPresent("org.springframework.web.servlet.DispatcherServlet", (ClassLoader)null) && !ClassUtils.isPresent("org.glassfish.jersey.servlet.ServletContainer", (ClassLoader)null)) {
            return REACTIVE;
        } else {
            String[] var0 = SERVLET_INDICATOR_CLASSES;
            int var1 = var0.length;

            for(int var2 = 0; var2 < var1; ++var2) {
                String className = var0[var2];
                if (!ClassUtils.isPresent(className, (ClassLoader)null)) {
                    return NONE;
                }
            }
            return SERVLET;
        }
    }
```

根据当前应用是否存在ClassPath中是否存在相关的实现类来判断应用类型。也可以直接使用SpringApplication或SpringApplicationBuilder的相关方法直接指定应用类型。

##### 加载应用上下文初始化容器

>  this.setInitializers(this.getSpringFactoriesInstances(ApplicationContextInitializer.class));

用于加载上下文初始化容器 **ApplicationContextInitializer.class**

```java
private <T> Collection<T> getSpringFactoriesInstances(Class<T> type, Class<?>[] parameterTypes, Object... args) {
    ClassLoader classLoader = this.getClassLoader();
    Set<String> names = new LinkedHashSet(SpringFactoriesLoader.loadFactoryNames(type, classLoader));
    List<T> instances = this.createSpringFactoriesInstances(type, parameterTypes, classLoader, args, names);
    AnnotationAwareOrderComparator.sort(instances);
    return instances;
}
```

getSpringFactoriesInstances方法使用Spring工厂加载机制，实例化***ApplicationContextInitializer***实现类，并进行排序，因此可以通过实现ApplicationContextInitializer接口用于在Spring Boot应用初始化之前执行一些操作

```java
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HelloApplicationContextInitializeImpl implements ApplicationContextInitializer {
    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        System.out.println("SpringBoot init : HelloApplicationContextInitializeImpl");
    }
}
```

注解 **@Order(Ordered.HIGHEST_PRECEDENCE)**会设置其优先级为最高

创建完成后，需要在工厂配置文件META-INF/spring.factories中配置该实现类

```
# initialized
org.springframework.context.ApplicationContextInitializer=\
com.pemila.boot.analysis.application.config.HelloApplicationContextInitializeImpl
```

启动应用可见在输出banner后立即输出了自定义的内容

```
SpringBoot init : HelloApplicationContextInitializeImpl
```

##### 加载应用事件监听器

> this.setListeners(this.getSpringFactoriesInstances(ApplicationListener.class));

与加载初始化器类似，Spring也功过工厂方法实例化 ApplicationListener的实现类。

监听的事件包含所有实现了 ***ApplicationEvent***接口的类

```
AfterDeleteEvent in KeyValueEvent
AfterDropKeySpaceEvent in KeyValueEvent
AfterGetEvent in KeyValueEvent
AfterInsertEvent in KeyValueEvent
AfterUpdateEvent in KeyValueEvent
ApplicationContextEvent
ApplicationContextInitializedEvent
ApplicationEnvironmentPreparedEvent
ApplicationFailedEvent
ApplicationPreparedEvent
ApplicationReadyEvent
ApplicationStartedEvent
ApplicationStartingEvent
BeforeDeleteEvent in KeyValueEvent
BeforeDropKeySpaceEvent in KeyValueEvent
....
```

自定义一个应用事件监听器：

```java
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AfterContextClosedListener implements ApplicationListener<ContextClosedEvent> {
    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        System.out.println("AfterContextClosed : 服务关闭");
    }
}

```

加入到工厂配置文件中：

```javascript
# initialized
org.springframework.context.ApplicationContextInitializer=\
com.pemila.boot.analysis.application.config.HelloApplicationContextInitializeImpl

# listener
org.springframework.context.ApplicationListener=\
com.pemila.boot.analysis.application.listener.AfterContextClosedListener
```

启动后可见输出：

```
2019-06-27 14:16:07.715  INFO 4256 --- [           main] ...
AfterContextClosed : 服务关闭

Process finished with exit code 0
```

##### 入口类推断

> this.mainApplicationClass = this.deduceMainApplicationClass();

用于推断应用程序的入口类。deduceMainApplicationClass源码：

```java 
private Class<?> deduceMainApplicationClass() {
    try {
        StackTraceElement[] stackTrace = (new RuntimeException()).getStackTrace();
        StackTraceElement[] var2 = stackTrace;
        int var3 = stackTrace.length;
        for(int var4 = 0; var4 < var3; ++var4) {
            StackTraceElement stackTraceElement = var2[var4];
            if ("main".equals(stackTraceElement.getMethodName())) {
                return Class.forName(stackTraceElement.getClassName());
            }
        }
    } catch (ClassNotFoundException var6) {
    }
    return null;
}
```

根据Main线程执行堆栈判断实际的入口类。

### SpringApplication运行阶段

运行阶段对应SpringApplication的run方法

```java
    public ConfigurableApplicationContext run(String... args) {
        // 开启时间监听
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ConfigurableApplicationContext context = null;
        Collection<SpringBootExceptionReporter> exceptionReporters = new ArrayList();
        this.configureHeadlessProperty();
        // 加载Spring应用运行监听器
        SpringApplicationRunListeners listeners = this.getRunListeners(args);
        listeners.starting();

        Collection exceptionReporters;
        try {
            ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
            ConfigurableEnvironment environment = this.prepareEnvironment(listeners, applicationArguments);
            this.configureIgnoreBeanInfo(environment);
            Banner printedBanner = this.printBanner(environment);
            context = this.createApplicationContext();
            exceptionReporters = this.getSpringFactoriesInstances(SpringBootExceptionReporter.class, new Class[]{ConfigurableApplicationContext.class}, context);
            this.prepareContext(context, environment, listeners, applicationArguments, printedBanner);
            this.refreshContext(context);
            this.afterRefresh(context, applicationArguments);
            stopWatch.stop();
            if (this.logStartupInfo) {
                (new StartupInfoLogger(this.mainApplicationClass)).logStarted(this.getApplicationLog(), stopWatch);
            }

            listeners.started(context);
            this.callRunners(context, applicationArguments);
        } catch (Throwable var10) {
            this.handleRunFailure(context, var10, exceptionReporters, listeners);
            throw new IllegalStateException(var10);
        }

        try {
            listeners.running(context);
            return context;
        } catch (Throwable var9) {
            this.handleRunFailure(context, var9, exceptionReporters, (SpringApplicationRunListeners)null);
            throw new IllegalStateException(var9);
        }
    }
```

##### 开启时间监听

```java
StopWatch stopWatch = new StopWatch();
stopWatch.start();
```

##### 开启运行监听器

```java
SpringApplicationRunListeners listeners = getRunListeners(args);
listeners.started();
```

***getRunListeners***方法源码：

```java
private SpringApplicationRunListeners getRunListeners(String[] args) {
    Class<?>[] types = new Class[]{SpringApplication.class, String[].class};
    return new SpringApplicationRunListeners(logger, this.getSpringFactoriesInstances(SpringApplicationRunListener.class, types, this, args));
}
```

通过`SpringFactoriesLoader`检索META-INF/spring.factories找到声明的所有`SpringApplicationRunListener`的实现类并将其实例化，然后装配到`List<SpringApplicationRunListener>`运行监听器集合中

***listeners.started()*** 用于遍历运行监听器集合中所有*SpringApplicationRunListener*的实现类，并逐一调用他们的starting方法，广播Spring boot应用要开始启动了。

*SpringApplicationRunListener*接口用于监听整个springboot应用的生命周期

```java
public interface SpringApplicationRunListener {
    // 应用刚启动
    void starting(); 
    // ConfigurableEnvironment准备完毕,允许将其调整
    void environmentPrepared(ConfigurableEnvironment environment); 
	// ConfigurableApplicationContext准备完毕，允许将其调整
    void contextPrepared(ConfigurableApplicationContext context);
    // ConfigurableApplicationContext已装载，但未启动
    void contextLoaded(ConfigurableApplicationContext context);
    // ConfigurableApplicationContext已启动，此时Spring Bean初始化已完成
    void started(ConfigurableApplicationContext context);
    // Spring 应用正在运行
    void running(ConfigurableApplicationContext context);
    // Spring 应用运行失败
    void failed(ConfigurableApplicationContext context, Throwable exception);
}
```

自定义一个SpringApplicationRunListener的实现类：

```java
public class HelloApplicationListener implements SpringApplicationRunListener {
    public HelloApplicationListener(SpringApplication application,String[] args){
        // 此构造方法为必须
        System.out.println("HelloApplicationListener加载");
    }
    @Override
    public void starting() {
        System.out.println("HelloApplicationRunListener starting......");
    }
    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
        System.out.println("environment 准备完毕");
    }
    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        System.out.println("context 准备完毕");
    }
    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        System.out.println("context 加载完毕");
    }
    @Override
    public void started(ConfigurableApplicationContext context) {
        System.out.println("context 已启动，spring Bean初始化完成");
    }
    @Override
    public void running(ConfigurableApplicationContext context) {
        System.out.println("spring应用运行中");
    }
    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        System.out.println("spring应用运行异常");
    }
}
```

在工厂配置文件中配置：

```
# run listener
org.springframework.boot.SpringApplicationRunListener=\
    com.pemila.boot.analysis.application.listener.HelloApplicationListener
```

运行结果：

```
HelloApplicationListener加载
HelloApplicationRunListener starting......
environment 准备完毕
SpringBoot init : HelloApplicationContextInitializeImpl 
context 准备完毕
2019-06-27 15:07:29.174  INFO 13708  Starting AnalysisSpringApplication on zhangchao-PC with PID 13708 (E:\IDEARepos\springboot-demo\boot-analysis-springapplication\target\classes started by user in E:\IDEARepos\springboot-demo)
2019-06-27 15:07:29.177  INFO 13708  The following profiles are active: dev
context 加载完毕
2019-06-27 15:07:30.042  INFO 13708  Started AnalysisSpringApplication in 1.324 seconds (JVM running for 2.342)
context 已启动，spring Bean初始化完成
spring应用运行中
AfterContextClosed : 服务关闭
```

##### 创建Environment

> ConfigurableEnvironment enviroment = this.prepareEnvironment(listeners,applicationArguments);

用于创建并配置当前应用将要使用的Environment(包括配置要使用的PropertySource以及Profile)

由于已经在准备阶段推断出了应用类型，只要根据响应的应用类型创建对应环境即可

应用类型与环境对应关系如下：

- Web Reactive：StandardReactiveWebEnvironment
- Web Servlet:  StandardServletEnvironment
- 非Web: StandardEnvironment

在`prepareEnvironment`方法中会执行`listeners.environmentPrepared(environment);`，用于遍历调用所有`SpringApplicationRunListener`实现类的`environmentPrepared()`方法，广播Environment准备完毕。

##### 是否打印banner

> Banner printedBanner = this.printBanner(environment);

##### 创建Context

> context = this.createApplicationContext();

```java
// 不同环境对应不同的ApplicationContext
protected ConfigurableApplicationContext createApplicationContext() {
    Class<?> contextClass = this.applicationContextClass;
    if (contextClass == null) {
        try {
            switch(this.webApplicationType) {
                case SERVLET:
                    contextClass = Class.forName("org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext");
                    break;
                case REACTIVE:
                    contextClass = Class.forName("org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebServerApplicationContext");
                    break;
                default:
                    contextClass = Class.forName("org.springframework.context.annotation.AnnotationConfigApplicationContext");
            }
        } catch (ClassNotFoundException var3) {
            throw new IllegalStateException("Unable create a default ApplicationContext, please specify an ApplicationContextClass", var3);
        }
    }
    return (ConfigurableApplicationContext)BeanUtils.instantiateClass(contextClass);
}
```

##### 装配Context

>  this.prepareContext(context, environment, listeners, applicationArguments, printedBanner);

```java
private void prepareContext(ConfigurableApplicationContext context, ConfigurableEnvironment environment, SpringApplicationRunListeners listeners, ApplicationArguments applicationArguments, Banner printedBanner) {
    
    // 为ApplicationContext加载了environment
    context.setEnvironment(environment);
    this.postProcessApplicationContext(context);
    // 逐一执行ApplicationContextInitializer的initalize方法
    this.applyInitializers(context);
    // 调用所有RunListener的contextPrepared
    listeners.contextPrepared(context);
    if (this.logStartupInfo) {
        this.logStartupInfo(context.getParent() == null);
        this.logStartupProfileInfo(context);
    }
    // 初始化IOC容器
    ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
    beanFactory.registerSingleton("springApplicationArguments", applicationArguments);
    if (printedBanner != null) {
        beanFactory.registerSingleton("springBootBanner", printedBanner);
    }
    if (beanFactory instanceof DefaultListableBeanFactory) {
        ((DefaultListableBeanFactory)beanFactory).setAllowBeanDefinitionOverriding(this.allowBeanDefinitionOverriding);
    }
    Set<Object> sources = this.getAllSources();
    Assert.notEmpty(sources, "Sources must not be empty");
    this.load(context, sources.toArray(new Object[0]));
    // 调用所有RunListener的contextLoaded，广播ApplicationContext加载完成
    // 其中包括通过@EnableAutoConfiguration导入的自动配置类
    listeners.contextLoaded(context);
}
```

##### Refresh Context

> this.refreshContext(context);

调用ApplicationContext的Refresh方法并初始化所有自动配置类

##### 广播应用已启动

>  listeners.started(context);

调用所有*SpringApplicationRunListener*的*started*方法广播Springboot应用已启动

##### 执行Runner

> this.callRunners(context, applicationArguments);

遍历所有的ApplicationRunner和CommandLineRunner，执行其run方法

可以实现自己的ApplicationRunner和CommandLineRunner来对SpringBoot的启动过程进行扩展

自定义一个ApplicationRunner：

```java
@Component
public class HelloWorldRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("Runner 执行");
    }
}
```



##### 广播应用运行中

> listeners.running(context);

用于调用*SpringApplicationRunListener*的running方法，广播Spring Boot应用正在运行中。

当run方法运行出现异常时，会调用*handleRunFailure*方法处理异常，该方法会通过*listeners.failed(context, exception)*调用RunListener的failed方法，广播应用启动失败，并将异常进行扩散

```java
try {
    listeners.running(context);
    return context;
} catch (Throwable var9) {
    this.handleRunFailure(context, var9, exceptionReporters, (SpringApplicationRunListeners)null);
    throw new IllegalStateException(var9);
}
```





