package ru.krista.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        Properties mailProperties = new Properties();
        mailProperties.put("mail.smtp.auth", true);
        mailProperties.put("mail.smtp.ssl.enable", true);
        mailProperties.put("mail.smtp.debug", true);

        mailSender.setJavaMailProperties(mailProperties);
        mailSender.setHost("smtp.yandex.ru");
        mailSender.setPort(465);
        mailSender.setProtocol("smtp");
        mailSender.setUsername("zaycev@krista-omsk.ru");
        mailSender.setPassword("123456");
        return mailSender;
    }
}
