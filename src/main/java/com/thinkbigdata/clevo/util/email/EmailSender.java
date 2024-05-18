package com.thinkbigdata.clevo.util.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
@RequiredArgsConstructor
public class EmailSender {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    public void sendMail(String email, String name, String password) throws MessagingException {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("password", password);
        String message = templateEngine.process("password.html", context);

        MimeMessage mail = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mail, false, "UTF-8");
        mimeMessageHelper.setFrom("clevomailservice@naver.com");
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("[CleVo] 임시 비밀번호 코드");
        mimeMessageHelper.setText(message, true);
        javaMailSender.send(mail);
    }
}
