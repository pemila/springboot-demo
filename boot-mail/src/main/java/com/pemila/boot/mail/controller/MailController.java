package com.pemila.boot.mail.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * @author 月在未央
 * @date 2019/6/18 10:01
 */
@RestController
public class MailController {

    @Autowired
    private JavaMailSender mailSender;


    @Value("${spring.mail.username}")
    private String from;

    private static final String TO = "1085824113@qq.com";

    /** 发送简单邮件*/
    @GetMapping("/send/simpleMail")
    public String sendSimpleMail() {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(from);
            mailMessage.setTo(TO);
            mailMessage.setSubject("邮件标题");
            mailMessage.setText("这是一封使用springboot发送的邮件");
            mailSender.send(mailMessage);
            return "发送成功";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /** 发送html格式邮件*/
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

    /** 发送邮件带附件*/
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


}
