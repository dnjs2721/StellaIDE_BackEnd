package shootingstar.stellaide.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shootingstar.stellaide.entity.User;
import shootingstar.stellaide.exception.CustomException;
import shootingstar.stellaide.exception.ErrorCode;
import shootingstar.stellaide.repository.user.UserRepository;
import shootingstar.stellaide.util.RedisUtil;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CheckDuplicateService duplicateService;
    private final RedisUtil redisUtil;

    @Transactional
    public void signup(String email, String password, String nickname) {
        duplicateService.checkDuplicateEmail(email);
        duplicateService.checkDuplicateNickname(nickname);

        if (redisUtil.hasKey(email) && redisUtil.getData(email).equals("validate")) {
            userRepository.save(new User(email, password, nickname));
            redisUtil.deleteData(email);
        } else {
            throw new CustomException(ErrorCode.VALIDATE_ERROR_EMAIL);
        }
    }
}
