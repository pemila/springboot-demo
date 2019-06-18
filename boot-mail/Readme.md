# 使用SpringBoot发送邮件

### 修改pom.xml

引入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

### 邮件配置

```yaml
server:
  port: 8080
spring:
  mail:
    host: smtp.126.com
    username: ********
    password: ********
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
            required: true
```

### 邮件发送

#### 发送简单邮件

```java
@Autowired
private JavaMailSender mailSender;

@Value("${spring.mail.username}")
private String from;

@GetMapping("/send/simpleMail")
public String sendSimpleMail() {
    try {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(from);
        mailMessage.setTo("1085824113@qq.com");
        mailMessage.setSubject("邮件标题");
        mailMessage.setText("这是一封使用springboot发送的邮件");
        mailSender.send(mailMessage);
        return "发送成功";
    } catch (Exception e) {
        e.printStackTrace();
        return e.getMessage();
    }
}
```

#### 发送html邮件

```java
@GetMapping("/send/htmlMail")
public String sendHtmlMail(){
    try {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(TO);
        helper.setSubject("html邮件");
        // 带html格式的内容
        helper.setText("<p style='color:#42b983'>使用Spring Boot发送HTML格式邮件。</p>",true);
        mailSender.send(message);
        return "发送成功";
    } catch (Exception e) {
        e.printStackTrace();
        return e.getMessage();
    }
}
```

#### 发送带附件的邮件

```java
@GetMapping("/send/mailWithAttach")
public String sendMailWithAttachment(){
    try {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(TO);
        helper.setSubject("带附件的邮件");
        helper.setText("<p style='color:#42b983'>请查看附件</p>",true);
        // 添加附件
        File files = new File(ClassLoader.getSystemClassLoader().getResource("static/11.jpg").getPath());
        helper.addAttachment("设计图标.jpg",files);
        mailSender.send(message);
        return "发送成功";
    } catch (Exception e) {
        e.printStackTrace();
        return e.getMessage();
    }
}
```

#### 发送模版邮件

##### 引入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

##### 创建模版

创建文件\resources\template\mailTemple.html

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8" />
    <title>模板</title>
</head>

<body>
    您好，您的验证码为<strong th:text="${code}"></strong>，请在两分钟内使用完成操作。
</body>
</html>
```

##### 发送

```java
@GetMapping("/send/templateMail")
public String sendTemplateMail(){
    try {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(TO);
        helper.setSubject("html模版邮件");
        // 定义模版中的变量内容
        Context context = new Context();
        context.setVariable("code","666666");
        // 将模版变量替换
        String template = templateEngine.process("mailTemplate",context);
        // 设置邮件内容
        helper.setText(template,true);
        mailSender.send(message);
        return "发送成功";
    } catch (Exception e) {
        e.printStackTrace();
        return e.getMessage();
    }
}
```

