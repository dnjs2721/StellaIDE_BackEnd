package shootingstar.stellaide.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shootingstar.stellaide.exception.CustomException;
import shootingstar.stellaide.repository.container.ContainerRepository;
import shootingstar.stellaide.repository.user.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static shootingstar.stellaide.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CheckDuplicateService {
    private final UserRepository userRepository;
    private final ContainerRepository containerRepository;

    private static final List<String> forbiddenWords = Arrays.asList("admin", "adm1n", "moderator", "banned");

    public void checkDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(DUPLICATE_EMAIL);
        }
    }

    public void checkDuplicateNickname(String nickname) {
        checkForbiddenNickname(nickname);
        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(DUPLICATE_NICKNAME);
        }
    }

    private void checkForbiddenNickname(String nickname) {
        // 영어(소문자) 와 숫자로만 이루어졌는지
        String regex = "^(?=.*[a-z])(?=.*\\d)[a-z\\d]+$";

        boolean matches = Pattern.matches(regex, nickname);
        if (!matches) {
            throw new CustomException(INCORRECT_FORMAT_NICKNAME);
        }

        // 정규 표현식과 매치되는지 확인, 금지어 사용 확인
        boolean forbidden = forbiddenWords.stream().noneMatch(nickname::contains);
        if (!forbidden) {
            throw new CustomException(FORBIDDEN_NICKNAME);
        }
    }

    public void checkDuplicateContainerName(String containerName) {
        checkForbiddenContainerName(containerName);
        if (containerRepository.existsByName(containerName)) {
            throw new CustomException(DUPLICATE_CONTAINER_NAME);
        }
    }

    private void checkForbiddenContainerName(String containerName) {
        // 알파벳, 숫자, 마침표(.), 밑줄(_), 하이픈(-)만 허용
        String regex = "^[\\w\\-_.]+$";

        boolean matches = Pattern.matches(regex, containerName);
        if (!matches) {
            throw new CustomException(INCORRECT_FORMAT_CONTAINER_NAME);
        }
    }

    public void checkForbiddenFileName(String fileName) {
        // 알파벳, 숫자, 마침표(.), 밑줄(_), 하이픈(-)만 허용
        String regex = "^[\\w\\-_.]+$";

        boolean matches = Pattern.matches(regex, fileName);
        if (!matches) {
            throw new CustomException(INCORRECT_FORMAT_FILE_NAME);
        }
    }
}
