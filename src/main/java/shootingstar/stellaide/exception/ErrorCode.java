package shootingstar.stellaide.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorCode {
    SERVER_ERROR(INTERNAL_SERVER_ERROR, "0000", "알 수 없는 오류가 발생했습니다."),
    AUTH_ERROR_EMAIL(BAD_REQUEST, "0001", "잘못된 키 혹은 잘못(만료) 된 인증 코드입니다."),
    VALIDATE_ERROR_EMAIL(BAD_REQUEST, "0002", "안증이 만료되었거나 인증되지 않은 이메일입니다."),
    INCORRECT_FORMAT(BAD_REQUEST, "0100", "잘못된 입력입니다."),
    INCORRECT_FORMAT_EMAIL(BAD_REQUEST, "1101", "잘못된 형식의 이메일입니다."),
    INCORRECT_FORMAT_CODE(BAD_REQUEST, "1102", "잘못된 형식의 인증코드입니다."),
    INCORRECT_FORMAT_NICKNAME(BAD_REQUEST, "1103", "잘못된 형식의 닉네임입니다."),
    INCORRECT_FORMAT_PASSWORD(BAD_REQUEST, "1104", "잘못된 형식의 비밀번호입니다."),
    DUPLICATE_EMAIL(CONFLICT, "1001", "이미 사용중인 이메일입니다."),
    DUPLICATE_NICKNAME(CONFLICT, "1002", "이미 사용중인 닉네임입니다."),
    FORBIDDEN_NICKNAME(FORBIDDEN, "1003", "허용되지 않는 닉네임입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String description;

    ErrorCode(HttpStatus httpStatus, String code, String description) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.description = description;
    }
}
