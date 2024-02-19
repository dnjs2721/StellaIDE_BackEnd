package shootingstar.stellaide.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import shootingstar.stellaide.service.dto.ProfileResDto;
import shootingstar.stellaide.entity.user.User;
import shootingstar.stellaide.exception.CustomException;
import shootingstar.stellaide.repository.user.UserRepository;
import shootingstar.stellaide.security.jwt.JwtTokenProvider;
import shootingstar.stellaide.util.SSHConnectionUtil;

import java.util.Optional;
import java.util.UUID;

import static shootingstar.stellaide.exception.ErrorCode.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final SSHConnectionUtil sshConnectionUtil;

    public ProfileResDto getProfile(String accessToken) {
        Authentication authentication = jwtTokenProvider.getAuthenticationFromAccessToken(accessToken);
        String userUuid = authentication.getName();

        User findUser = findUserByUUID(userUuid);

        String profileImgUrl = null;
        if (findUser.getProfileImg() != null) {
             profileImgUrl = sshConnectionUtil.getProfileImgUrl(findUser.getProfileImg());
        }

        return new ProfileResDto(findUser.getEmail(), findUser.getNickname(), profileImgUrl);
    }

    @Transactional
    public void changeProfileImg(MultipartFile file, String accessToken) {
        Authentication authentication = jwtTokenProvider.getAuthenticationFromAccessToken(accessToken);
        String userUuid = authentication.getName();

        User findUser = findUserByUUID(userUuid);
        if (!file.isEmpty()) {
            String fileName = findUser.getNickname() + "_img.png";
            sshConnectionUtil.uploadProfileImg(file, fileName);
            findUser.changeProfileImg(fileName);
        }  else {
            throw new CustomException(PROFILE_IMG_FILE_IS_EMPTY);
        }
    }

    // accessToken 의 사용자 고유번호를 통해 사용자 검색
    private User findUserByUUID(String userUuid) {
        Optional<User> optionalUser = userRepository.findById(UUID.fromString(userUuid));
        if (optionalUser.isEmpty()) {
            // 엑세스 토큰을 통해 사용자를 찾지 못했을 때
            // 이 오류가 발생한다면 이미 탈퇴한 회원이 만료되지 않은 엑세스 토큰을 통해 비밀번호 확인을 시도했거나
            // 어떠한 방법으로 JWT 토큰의 사용자 고유번호를 변경했을 때
            throw new CustomException(USER_NOT_FOUND);
        }
        return optionalUser.get();
    }
}
