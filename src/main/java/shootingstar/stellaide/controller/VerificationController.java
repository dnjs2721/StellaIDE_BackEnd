package shootingstar.stellaide.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import shootingstar.stellaide.controller.dto.SendEmailRequestDto;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/verification")
public class VerificationController {

    /**
     * 인증 메일 전송
     */
    @PostMapping("/send-email")
    public void sendEmail(@RequestBody @Valid SendEmailRequestDto data) {
        System.out.println(data.getEmail());
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
}
