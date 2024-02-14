package shootingstar.stellaide.controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shootingstar.stellaide.controller.dto.emailVerification.SendEmailReqDto;
import shootingstar.stellaide.controller.dto.emailVerification.VerificationEmailReqDto;
import shootingstar.stellaide.service.MailService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/verification")
public class EmailVerificationController {

    private final MailService mailService;

    /**
     * 인증 메일 전송
     */
    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(@RequestBody @Valid SendEmailReqDto req) throws MessagingException {
        mailService.sendAuthCode(req.getEmail());
        return ResponseEntity.ok("메일 발송에 성공하였습니다.");
    }

    /**
     * 메일 인증
     */
    @PostMapping("/email")
    public ResponseEntity<String> checkCode(@RequestBody @Valid VerificationEmailReqDto req) {
        mailService.validateCode(req.getEmail(), req.getCode());
        return ResponseEntity.ok("인증에 성공하였습니다.");
    }
}