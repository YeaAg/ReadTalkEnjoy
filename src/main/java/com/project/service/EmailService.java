package com.project.service;

import com.project.mapper.UserMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Random;

@Log4j2
@Service
//@PropertySource("classpath:my.properties")
public class EmailService {
    // 원래 에러 뜹니다 but 문제 없습니다 -> application.properties에 없고, my.properties에 있어서 그렇습니다
    @Autowired private JavaMailSender mailSender;
    @Autowired private TemplateEngine templateEngine;
    @Autowired private HttpSession session;

    private final String FROM;
    private String EMAIL_CERT_TEMPLATE;
    @Autowired
    private UserMapper userMapper;

    public EmailService(
            @Value("${spring.mail.username}") String from
    ) {
        this.FROM = from;
        log.info("FROM : " + FROM); // application.properties에서 설정해주세요
    }

    public String send_cert_mail(String TO, String requestAuth, String id) throws MessagingException {
        Context context = new Context();

        if (requestAuth.equals("join")) {
            StringBuilder certNumber = new StringBuilder();
            Random rand = new Random();
            for (int i = 0; i < 6; i++) {
                certNumber.append(rand.nextInt(10));
            }
            context.setVariable("certNumber", certNumber.toString());
            EMAIL_CERT_TEMPLATE = "/mail/email-auth-template.html";
            send_mail(EMAIL_CERT_TEMPLATE, context, "책 토론 회원가입 인증 메일입니다.", TO);
            log.info("회원가입 이메일 전송됨");
            return certNumber.toString();
        }
        else if(requestAuth.equals("find-id")) {
            StringBuilder certNumber = new StringBuilder();
            Random rand = new Random();
            for (int i = 0; i < 6; i++) {
                certNumber.append(rand.nextInt(10));
            }
            context.setVariable("certNumber", certNumber.toString());
            EMAIL_CERT_TEMPLATE = "/mail/id-email-auth-template.html";
            send_mail(EMAIL_CERT_TEMPLATE, context, "책 토론 아이디 찾기 인증 메일입니다.", TO);
            log.info("아이디 찾기 이메일 전송됨");
            return certNumber.toString();
        }
        else{
            if(id == null)
                return null;
            String foundId = userMapper.getUserById(id).getId();
            if(foundId == null)
                return null;

            String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            StringBuilder code = new StringBuilder();
            Random random = new Random();

            for (int i = 0; i < 10; i++) {
                int index = random.nextInt(characters.length());
                code.append(characters.charAt(index));
            }

            String passwordLink = "http://localhost:8080/reset-pw-2?code=" + code;
            session.setAttribute("code", code.toString());
            session.setAttribute("id", id);
            context.setVariable("passwordLink", passwordLink);
            EMAIL_CERT_TEMPLATE = "/mail/pw-email-auth-template.html";
            send_mail(EMAIL_CERT_TEMPLATE, context, "책 토론 비밀번호 변경 링크 메일입니다.", TO);
            log.info("비밀번호 링크 이메일 전송됨");
            return code.toString();
        }

    }

    public void send_mail(String template, Context context, String SUBJECT, String TO) throws MessagingException {
        String templateMailContext = templateEngine.process(template, context);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessageHelper.setFrom(FROM);
        mimeMessageHelper.setTo(TO);
        mimeMessageHelper.setSubject(SUBJECT);
        mimeMessageHelper.setText(templateMailContext, true);
        mailSender.send(mimeMessage);
    }
}
