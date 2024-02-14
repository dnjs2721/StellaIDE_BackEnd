package shootingstar.stellaide.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import shootingstar.stellaide.entity.User;
import shootingstar.stellaide.exception.CustomException;
import shootingstar.stellaide.exception.ErrorCode;
import shootingstar.stellaide.repository.user.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    // 사용자 이름으로 사용자 조회
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(this::createUserDetails)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_AT_LOGIN));
    }

    // 사용자 고유번호로 사용자 조회
    public UserDetails loadUserByUserId(String uuid) {
        return userRepository.findById(UUID.fromString(uuid))
                .map(this::createUserDetails)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_AT_LOGIN));
    }

    // 해당하는 User 의 데이터가 존재한다면 UserDetails 객체로 만들어서 리턴
    private UserDetails createUserDetails(User user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
}
