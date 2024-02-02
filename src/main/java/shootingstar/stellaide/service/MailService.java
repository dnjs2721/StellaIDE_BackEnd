package shootingstar.stellaide.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import shootingstar.stellaide.util.RedisUtil;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final RedisUtil redisUtil;

    @Value("${fromMail}")
    private String fromEmail;

    public void sendAuthCode(String email) throws MessagingException {
        String title = "Stella-IDE 이메일 인증 번호"; // 제목

        MimeMessage message = mailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, email);
        message.setSubject(title);
        message.setFrom(fromEmail);
        message.setText(setContextByAuthCode("12345"), "utf-8", "html");

        // 메시지 전송
        mailSender.send(message);
        redisUtil.setData(email, "12345", 300000L);
    }

    public String setContextByAuthCode(String code) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process("mail", context);
    }
}
