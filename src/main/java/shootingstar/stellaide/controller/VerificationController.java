package shootingstar.stellaide.controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import shootingstar.stellaide.controller.dto.verification.SendEmailRequestDto;
import shootingstar.stellaide.controller.dto.verification.VerificationEmailRequestDto;
import shootingstar.stellaide.service.MailService;

import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/verification")
public class VerificationController {

    private final MailService mailService;

    /**
     * 인증 메일 전송
     */
    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(@RequestBody @Valid SendEmailRequestDto data) throws MessagingException {
        mailService.sendAuthCode(data.getEmail());
        return ResponseEntity.ok("메일 발송에 성공하였습니다.");
    }

    /**
     * 메일 인증
     */
    @PostMapping("/email")
    public ResponseEntity<String> checkCode(@RequestBody @Valid VerificationEmailRequestDto data) {
        mailService.validateCode(data.getEmail(), data.getCode());
        return ResponseEntity.ok("인증에 성공하였습니다.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ArrayList<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();

        ArrayList<String> res = new ArrayList<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            String message = "[" +
                    fieldError.getField() +
                    "](은)는 " +
                    fieldError.getDefaultMessage() +
                    " 입력된 값: [" +
                    fieldError.getRejectedValue() +
                    "]";

            res.add(message);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
