package shootingstar.stellaide.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static shootingstar.stellaide.exception.ErrorCode.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();

        ErrorCode errorCode = INCORRECT_FORMAT;
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            switch (fieldError.getField()) {
                case "email" -> {
                    errorCode = INCORRECT_FORMAT_EMAIL;
                    break;
                }
                case "code" -> {
                    errorCode = INCORRECT_FORMAT_CODE;
                    break;
                }
                case "nickname" -> {
                    errorCode = INCORRECT_FORMAT_NICKNAME;
                    break;
                }
                case "password" -> {
                    errorCode = INCORRECT_FORMAT_PASSWORD;
                    break;
                }
            }

            if (!errorCode.equals(INCORRECT_FORMAT)) {
                break;
            }
        }

        return ResponseEntity.status(errorCode.getHttpStatus()).body(new ErrorResponse(errorCode));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomExceptionHandler(CustomException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ErrorResponse errorResponse = new ErrorResponse(errorCode);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
    }
}
