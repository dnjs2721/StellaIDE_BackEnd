package shootingstar.stellaide.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;


@Getter
public enum ErrorCode {
    SERVER_ERROR(INTERNAL_SERVER_ERROR, "0000", "알 수 없는 오류가 발생했습니다."),
    INCORRECT_FORMAT(BAD_REQUEST, "0001", "잘못된 입력입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED,"0002", "제공되지 않는 메서드입니다."),
    STORAGE_ERROR(INTERNAL_SERVER_ERROR,"0003", "저장소 연결에 실패하였습니다."),
    STORAGE_COMMEND_ERROR(INTERNAL_SERVER_ERROR,"0004", "명령어 실행에 실패하였습니다."),

    AUTHENTICATION_ERROR(UNAUTHORIZED, "0100", "인증에 실패하였습니다."),
    ACCESS_DENIED(FORBIDDEN, "0101", "잘못된 접근입니다."),
    INVALID_ACCESS_TOKEN(FORBIDDEN, "0102", "잘못된 Access Token 입니다."),
    EXPIRED_ACCESS_TOKEN(FORBIDDEN, "0103", "만료된 Access Token 입니다."),
    UNSUPPORTED_ACCESS_TOKEN(FORBIDDEN, "0104", "지원하지 않는 Access Token 입니다."),
    ILLEGAL_ACCESS_TOKEN(FORBIDDEN, "0105", "Claim이 빈 Access Token 입니다."),
    INVALID_REFRESH_TOKEN(FORBIDDEN, "0106", "잘못된 Refresh Token 입니다."),
    EXPIRED_REFRESH_TOKEN(FORBIDDEN, "0107", "만료된 Refresh Token 입니다."),
    UNSUPPORTED_REFRESH_TOKEN(FORBIDDEN, "0108", "지원하지 않는 Refresh Token 입니다."),
    ILLEGAL_REFRESH_TOKEN(FORBIDDEN, "0109", "Claim이 빈 Refresh Token 입니다."),

    NOT_FOUND_END_POINT(NOT_FOUND, "0200", "존재하지 않는 접근입니다."),

    INCORRECT_FORMAT_EMAIL(BAD_REQUEST, "1001", "잘못된 형식의 이메일입니다."),
    INCORRECT_FORMAT_CODE(BAD_REQUEST, "1002", "잘못된 형식의 인증코드입니다."),
    INCORRECT_FORMAT_NICKNAME(BAD_REQUEST, "1003", "잘못된 형식의 닉네임입니다."),
    INCORRECT_FORMAT_PASSWORD(BAD_REQUEST, "1004", "잘못된 형식의 비밀번호입니다."),

    AUTH_ERROR_EMAIL(UNAUTHORIZED, "1101", "잘못된 키 혹은 잘못(만료) 된 인증 코드입니다."),
    VALIDATE_ERROR_EMAIL(UNAUTHORIZED, "1102", "인증이 만료되었거나 인증되지 않은 이메일입니다."),
    USER_NOT_FOUND_AT_LOGIN(CONFLICT, "1103", "존재하지 않는 사용자 이거나 잘못된 패스워드입니다."), // 로그인시 발생하는 오류
    INCORRECT_VALUE_PASSWORD(CONFLICT, "1104", "잘못된 패스워드입니다."), // 사용자 수정시 발생하는 오류

    USER_NOT_FOUND(NOT_FOUND, "1201", "존재하지 않는 사용자입니다."), // 로그인을 제외한 사용자 확인에서 발생하는 오류
    PROFILE_IMG_FILE_IS_EMPTY(NOT_FOUND, "1202", "빈 프로필 이미지 파일입니다."),

    DUPLICATE_EMAIL(CONFLICT, "1301", "이미 사용중인 이메일입니다."),
    DUPLICATE_NICKNAME(CONFLICT, "1302", "이미 사용중인 닉네임입니다."),
    FORBIDDEN_NICKNAME(FORBIDDEN, "1303", "허용되지 않는 닉네임입니다."),
    PASSWORD_CURRENTLY_IN_USE(CONFLICT, "1304", "현재 사용중인 패스워드입니다. 다른 패스워드로 입력바랍니다."),
    NOT_SUPPORT_IMG_TYPE(CONFLICT, "1305", "지원하지 않는 이미지 타입입니다. PNG 파일을 사용해주세요."),

    INCORRECT_FORMAT_CONTAINER_ID(BAD_REQUEST, "2000", "잘못된 형식의 컨테이너 고유번호입니다."),
    INCORRECT_FORMAT_CONTAINER_NAME(BAD_REQUEST, "2001", "잘못된 형식의 컨테이너 이름입니다."),
    INCORRECT_FORMAT_CONTAINER_TYPE(BAD_REQUEST, "2002", "잘못된 형식의 컨테이너 타입입니다."),
    INCORRECT_FORMAT_CONTAINER_DES(BAD_REQUEST, "2003", "잘못된 형식의 컨테이너 설명입니다."),
    INCORRECT_FORMAT_FILE_PATH(BAD_REQUEST, "2004", "잘못된 형식의 파일(디렉토리) 경로입니다."),
    INCORRECT_FORMAT_FILE_NAME(BAD_REQUEST, "2005", "잘못된 형식의 파일(디렉토리) 이름입니다."),

    DENIED_PERMISSION_CONTAINER(CONFLICT, "2100", "해당 컨테이너에 대한 권한이 없습니다."),

    NOT_FOUND_CONTAINER(NOT_FOUND, "2200", "존재하지 않는 컨테이너입니다."),
    NOT_FOUND_SHARED_USER_CONTAINER(NOT_FOUND, "2201", "존재하지 않는 사용자 공유입니다."),

    MAX_CONTAINER_ERROR(CONFLICT, "2301", "컨테이너는 최대 5개까지 생성 가능합니다"),
    DUPLICATE_CONTAINER_NAME(CONFLICT, "2302", "이미 사용중인 컨테이너 이름입니다."),
    MAX_SHARED_ERROR(CONFLICT, "2303", "최대 5명까지 공유 가능합니다."),
    ALREADY_SHARED_ERROR(CONFLICT, "2304", "이미 공유된 사용자입니다."),
    FAILED_SHARED_ERROR(CONFLICT, "2305", "자신은 공유 대상자가 아닙니다."),
    FAILED_LOAD_CONTAINER_TREE(INTERNAL_SERVER_ERROR, "2306", "컨테이너 트리 정보를 가지고 오는데 실패하였습니다."),
    NOT_SUPPORT_CONTAINER_TYPE(CONFLICT, "2307", "지원하지 않는 컨테이너 타입입니다."),

    WEB_SOCKET_ERROR(BAD_REQUEST,"3000", "잘못된 형식의 웹소켓입니다."),
    INCORRECT_FORMAT_ROOM_TYPE(BAD_REQUEST, "3001", "잘못된 채팅방 타입입니다."),
    INCORRECT_FORMAT_ROOM_ID(BAD_REQUEST, "3002", "잘못된 채팅방 고유번호입니다."),

    NOT_FOUND_CHAT_ROOM(NOT_FOUND, "3201", "존재하지 않는 채팅방입니다."),

    WEB_SOCKET_MESSAGE_SEND_ERROR(BAD_REQUEST,"3300", "메세지 전송에 실패하였습니다."),
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
