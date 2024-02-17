package shootingstar.stellaide.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shootingstar.stellaide.controller.dto.user.ChangeProfileImageReqDto;
import shootingstar.stellaide.service.dto.ProfileResDto;
import shootingstar.stellaide.exception.CustomException;
import shootingstar.stellaide.exception.ErrorCode;
import shootingstar.stellaide.service.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    @GetMapping("/profile")
    public ResponseEntity<ProfileResDto> getProfile(HttpServletRequest request) {
        String accessToken = getTokenFromHeader(request);
        ProfileResDto profile = userService.getProfile(accessToken);
        return ResponseEntity.ok().body(profile);
    }

    @PatchMapping("/changeProfileImage")
    public ResponseEntity<String> handleFileUpload(@Valid @ModelAttribute ChangeProfileImageReqDto reqDto, HttpServletRequest request) {
        String accessToken = getTokenFromHeader(request);
        userService.changeProfileImg(reqDto.getProfileImgFile(), accessToken);
        return ResponseEntity.ok().body("프로필 이미지가 성공적으로 변경되었습니다.");
    }

    // 헤더에서 엑세스 토큰을 추출하는 메서드
    private static String getTokenFromHeader(HttpServletRequest request) {
        String token = request.getHeader("Authorization"); // 헤더에 존재하는 엑세스 토큰을 받아온다.

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // "Bearer " 접두어 제거
        } else {
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        }
        return token;
    }
}
