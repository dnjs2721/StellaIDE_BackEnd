package shootingstar.stellaide.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

import static shootingstar.stellaide.exception.ErrorCode.*;

@Slf4j
@RestControllerAdvice
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
                case "nickname", "userNickname" -> {
                    errorCode = INCORRECT_FORMAT_NICKNAME;
                    break;
                }
                case "password", "newPassword" -> {
                    errorCode = INCORRECT_FORMAT_PASSWORD;
                    break;
                }
                case "profileImgFile" -> {
                    errorCode = PROFILE_IMG_FILE_IS_EMPTY;
                    break;
                }
                case "containerId" -> {
                    errorCode = INCORRECT_FORMAT_CONTAINER_ID;
                    break;
                }
                case "containerType" -> {
                    errorCode = INCORRECT_FORMAT_CONTAINER_TYPE;
                    break;
                }
                case "containerName" -> {
                    errorCode = INCORRECT_FORMAT_CONTAINER_NAME;
                    break;
                }
                case "containerDescription" -> {
                    errorCode = INCORRECT_FORMAT_CONTAINER_DES;
                    break;
                }
                case "filePath", "path" -> {
                    errorCode = INCORRECT_FORMAT_FILE_PATH;
                    break;
                }
                case "fileName" , "directoryName" -> {
                    errorCode = INCORRECT_FORMAT_FILE_NAME;
                    break;
                }
            }

            if (!errorCode.equals(INCORRECT_FORMAT)) {
                break;
            }
        }

        return ResponseEntity.status(errorCode.getHttpStatus()).body(new ErrorResponse(errorCode));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException exception) {
        Map<String, String> fieldErrors = new HashMap<>();

        // 각 제약 조건 위반을 순회하며 필드 이름과 오류 메시지를 맵에 저장
        exception.getConstraintViolations().forEach(violation -> {
            String field = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            fieldErrors.put(field, message);
        });

        ErrorCode errorCode = INCORRECT_FORMAT;

        for (Map.Entry<String, String> entry : fieldErrors.entrySet()) {
            switch (entry.getKey()) {
                case "containerId" -> {
                    errorCode = INCORRECT_FORMAT_CONTAINER_ID;
                    break;
                }
                case "roomId" -> {
                    errorCode = INCORRECT_FORMAT_ROOM_ID;
                    break;
                }
                case "userNickName" -> {
                    errorCode = INCORRECT_FORMAT_NICKNAME;
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
        log.info(exception.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleCustomExceptionHandler(NoHandlerFoundException exception) {
        log.info(exception.getMessage());
        ErrorCode errorCode = NOT_FOUND_END_POINT;
        ErrorResponse errorResponse = new ErrorResponse(errorCode);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleCustomExceptionHandler(HttpMessageNotReadableException exception) {
        log.info(exception.getMessage());
        ErrorCode errorCode = INCORRECT_FORMAT;
        ErrorResponse errorResponse = new ErrorResponse(errorCode);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleCustomExceptionHandler(HttpRequestMethodNotSupportedException exception) {
        log.info(exception.getMessage());
        ErrorCode errorCode = METHOD_NOT_ALLOWED;
        ErrorResponse errorResponse = new ErrorResponse(errorCode);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(InternalError.class)
    public ResponseEntity<ErrorResponse> handleCustomExceptionHandler(InternalError exception) {
        log.info(exception.getMessage());
        ErrorCode errorCode = SERVER_ERROR;
        ErrorResponse errorResponse = new ErrorResponse(errorCode);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleCustomExceptionHandler(RuntimeException exception) {
        log.info(exception.getMessage());
        ErrorCode errorCode = SERVER_ERROR;
        ErrorResponse errorResponse = new ErrorResponse(errorCode);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
    }
}
