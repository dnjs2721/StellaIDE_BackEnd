package shootingstar.stellaide.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import shootingstar.stellaide.entity.User;
import shootingstar.stellaide.exception.CustomException;
import shootingstar.stellaide.repository.user.UserRepository;
import shootingstar.stellaide.security.jwt.JwtTokenProvider;
import shootingstar.stellaide.security.jwt.TokenInfo;
import shootingstar.stellaide.util.JwtRedisUtil;
import shootingstar.stellaide.util.LoginListRedisUtil;
import shootingstar.stellaide.util.MailRedisUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static shootingstar.stellaide.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserAuthService {
    private final UserRepository userRepository;
    private final CheckDuplicateService duplicateService;

    private final JwtRedisUtil jwtRedisUtil;
    private final MailRedisUtil mailRedisUtil;
    private final LoginListRedisUtil loginListRedisUtil;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원 가입
    @Transactional
    public void signup(String email, String password, String nickname) {
        duplicateService.checkDuplicateEmail(email); // 이메일 중복 검사
        duplicateService.checkDuplicateNickname(nickname); // 닉네임 검사

        if (mailRedisUtil.hasKey(email) && mailRedisUtil.getData(email).equals("validate")) { // 이메일 인증을 받은 이메일 인지 확인
            String encodePassword = passwordEncoder.encode(password); // 패스워드 암호화
            userRepository.save(new User(email, encodePassword, nickname));
            mailRedisUtil.deleteData(email); // 암호화된 사용자 디비에 저장
        } else {
            throw new CustomException(VALIDATE_ERROR_EMAIL);
        }
    }

    // 로그인
    public TokenInfo login(String email, String password, String oldRefreshToken) {
        if (oldRefreshToken != null) { // 만약 발급 받은 리프레시 토큰이 있다면
            String data = jwtRedisUtil.getData(oldRefreshToken); // 해당 리프레시 토큰 무효화
            if (data != null) {
                try {
                    expiredRefreshToken(oldRefreshToken, data);
                } catch (Exception e) {
                    // JSON 파싱 중 오류 처리
                    throw new CustomException(INVALID_REFRESH_TOKEN);
                }
            }
        }

        // JWT 발급
        try {
            // 이메일과 해스워드를 통해 사용자 인증정보 생성 비밀번호가 틀릴시 예외 발생
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            String username = authentication.getName(); // DB 사용자 고유 번호

            // 생성한 사용자 인증정보를 통해 사로운 JWT 토큰 생성
            TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

            // 리프레시 토큰의 만료 시간
            long expireTimeInMilliseconds = tokenInfo.getRefreshTokenExpire();

            // 리프레시 토큰의 만료 시간을 Date 객체로 변환
            Date expireDate = new Date(expireTimeInMilliseconds);

            // Date 객체를 원하는 형식으로 포맷팅
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String formattedDate = dateFormat.format(expireDate);

            // JWT redis 에 저장될 value 정보 생성
            String valueToStore = String.format("{\"status\":\"%s\", \"expireTime\":\"%s\"}", "active", formattedDate);

            // JWT redis 에 key : 리프레시 토큰, value : 상태, 만료시간 을 저장
            jwtRedisUtil.setDataExpire(tokenInfo.getRefreshToken(), valueToStore , 20 * 60 * 1000);

            // loginList redis 에 자장될 value 생성
            String nowValueToStore = String.format("{\"loginTime\":\"%s\"}", dateFormat.format(new Date()));
            // Login Redis 에 key : 사용자 고유번호, value : 마지막 활동시간 을 저장
            loginListRedisUtil.setDataExpire(username, nowValueToStore, 10 * 60 * 1000);

            return tokenInfo;
        } catch (AuthenticationException e) {
            // 인증 실패 시의 처리 로직
            throw new CustomException(USER_NOT_FOUND);
        }
    }

    // 로그 아웃
    public String logout(String refreshToken) {
        // jwt redis 에서 해당 토큰의 정보를 가지고 온다.
        String data = jwtRedisUtil.getData(refreshToken);
        String userName;
        if (data != null) {
            // JSON 문자열을 객체로 변환 (예시는 Jackson 라이브러리 사용)
            try {
                // 해당 토큰을 복호화 하여 정보를 가지고 온다.
                Authentication authentication = jwtTokenProvider.getAuthenticationFromRefreshToken(refreshToken);
                userName = authentication.getName(); // 가져온 정보에서 사용자 고유번호를 얻는다.
                loginListRedisUtil.deleteData(userName); // 로그인 리스트에서 해당 사용자를 삭제한다.

                expiredRefreshToken(refreshToken, data); // 해당 토큰을 무효화 시킨다.
            } catch (Exception e) {
                // JSON 파싱 중 오류 처리
                throw new CustomException(INVALID_REFRESH_TOKEN);
            }
        } else {
            throw new CustomException(INVALID_REFRESH_TOKEN);
        }
        return userName;
    }

    // 리프레시 토큰 무효화 검증
    public boolean checkRefreshTokenState(String token) {
        // jwt redis 에서 토큰의 정보를 가지고 온다.
        String data = jwtRedisUtil.getData(token);
        if (data != null) {
            try {
                // JSON 문자열을 파싱하기 위한 ObjectMapper 객체 생성
                ObjectMapper objectMapper = new ObjectMapper();
                // JSON 문자열을 JsonNode로 파싱
                JsonNode jsonNode = objectMapper.readTree(data);

                // "status" 필드의 값을 가져옴
                String status = jsonNode.get("status").asText();

                // status 값이 active 가 아닌 경우 무효화 된 토큰이다.
                return status.equals("active");
            } catch (Exception e) {
                // JSON 파싱 중 오류 처리
                throw new CustomException(INVALID_REFRESH_TOKEN);
            }
        } else {
            throw new CustomException(INVALID_REFRESH_TOKEN);
        }
    }

    // 엑세스 토큰 재발급
    public String refreshAccessToken(String refreshToken) {
        // 리프레시 토큰을 복호화 하여 정보를 얻는다.
        Authentication authentication = jwtTokenProvider.getAuthenticationFromRefreshToken(refreshToken);
        // 새 엑세스 토큰 생성
        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication, new Date().getTime());

        // 로그인 리스트에 해당 사용자를 업데이트 한다.
        String name = authentication.getName();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String nowValueToStore = String.format("{\"loginTime\":\"%s\"}", dateFormat.format(new Date()));
        loginListRedisUtil.setDataExpire(name, nowValueToStore, 10 * 60 * 1000);

        return newAccessToken;
    }

    // 로그 아웃
    @Transactional
    public void deleteUser(String username) {
        // 사용자 고유번호를 통해 사용자를 검색한다.
        Optional<User> optionalUser = userRepository.findByUserId(UUID.fromString(username));
        if (optionalUser.isEmpty()) {
            throw new CustomException(USER_NOT_FOUND);
        }
        User user = optionalUser.get();
        userRepository.delete(user); // 해당 사용자를 삭제한다.
    }

    // 리프레시 토큰 무효화 메서드
    private void expiredRefreshToken(String refreshToken, String data) throws JsonProcessingException, ParseException {
        ObjectMapper objectMapper = new ObjectMapper();
        // 토큰 데이터를 JSON 형태로 변환한다,
        JsonNode tokenData = objectMapper.readTree(data);

        // 상태 정보를 expired 로 변경한다.
        ((ObjectNode) tokenData).put("status", "expired");
        // JSON 형태를 다시 string 으로 변환 한다.
        String updatedTokenDataJson = tokenData.toString();

        // 현재 토큰의 만료 시간을 체크한다.
        String expireTimeString = tokenData.get("expireTime").asText();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date expireTime = sdf.parse(expireTimeString);
        Date now = new Date();

        // 현재를 기준으로 만료까지 남은 시간을 계산한다.
        long secondsLeft = expireTime.getTime() - now.getTime();

        // 업데이트된 정보와 계산된 만료 시간을 Redis에 저장
        if (secondsLeft > 0) {
            jwtRedisUtil.setDataExpire(refreshToken, updatedTokenDataJson, secondsLeft);
        } else {
            // 이미 만료된 경우, Redis에서 해당 토큰 삭제
            jwtRedisUtil.deleteData(refreshToken);
        }
    }
}
