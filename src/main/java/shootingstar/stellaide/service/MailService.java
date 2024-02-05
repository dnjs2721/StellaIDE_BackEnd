package shootingstar.stellaide.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import shootingstar.stellaide.exception.CustomException;
import shootingstar.stellaide.exception.ErrorCode;
import shootingstar.stellaide.util.RedisUtil;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final RedisUtil redisUtil;

    @Value("${fromMail}")
    private String fromEmail;

    public void sendAuthCode(String email) throws MessagingException {
        String code = createCode();

        String title = "Stella-IDE 이메일 인증 번호"; // 제목
        MimeMessage message = mailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, email);
        message.setSubject(title);
        message.setFrom(fromEmail);
        message.setText(setContextByAuthCode(code), "utf-8", "html");

        // 메시지 전송
        mailSender.send(message);
        redisUtil.setDataExpire(email, code, 5);
    }

    public void validateCode(String key, String value) {
        if (redisUtil.hasKey(key) && redisUtil.getData(key).equals(value)) {
            redisUtil.deleteData(key);
            redisUtil.setDataExpire(key, "validate", 15);
        } else {
            throw new CustomException(ErrorCode.AUTH_ERROR_EMAIL);
        }
    }

    public String createCode() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i <8; i++) {
            int index = random.nextInt(3); // 0 ~ 2 랜덤 index -> case 문

            switch (index) {
                case 0 -> key.append((char) (random.nextInt(26) + 97)); // 대문자
                case 1 -> key.append((char) (random.nextInt(26) + 65)); // 소문자
                case 2 -> key.append(random.nextInt(9)); // 숫자
            }
        }
        return key.toString();
    }

    public String setContextByAuthCode(String code) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process("mail", context);
    }
}
