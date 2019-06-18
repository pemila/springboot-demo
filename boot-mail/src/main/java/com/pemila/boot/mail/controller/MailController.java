package com.pemila.boot.mail.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
