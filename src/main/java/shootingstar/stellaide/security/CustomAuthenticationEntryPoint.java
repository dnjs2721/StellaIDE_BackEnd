package shootingstar.stellaide.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import shootingstar.stellaide.exception.CustomException;
import shootingstar.stellaide.exception.ErrorCode;
import shootingstar.stellaide.exception.ErrorResponse;

import java.io.IOException;

@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ObjectMapper objectMapper = new ObjectMapper();

        log.info(request.getRequestURI());
        log.info(authException.getMessage());

        ErrorCode errorCode = ErrorCode.AUTHENTICATION_ERROR;
        HttpStatus httpStatus = errorCode.getHttpStatus();
        ErrorResponse errorResponse = new ErrorResponse(errorCode);

        response.setStatus(httpStatus.value()); // HTTP 상태 코드 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse)); // JSON 형태로 에러 응답 작성
    }
}
