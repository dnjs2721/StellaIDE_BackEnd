package shootingstar.stellaide.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shootingstar.stellaide.entity.user.User;
import shootingstar.stellaide.repository.user.UserRepository;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    public void save() throws Exception {
        //given
        User user = new User(
                "skadu66@gmail.com",
                "test",
                "test"
        );

        //when
        User saveUser = userRepository.save(user);
        userRepository.flush();

        UUID userId = user.getUserId();
        System.out.println(userId);

        UUID findUserId = userRepository.findById(userId).get().getUserId();

        //then
        assertThat(saveUser.getUserId()).isEqualTo(userId);
        assertThat(userId).isEqualTo(findUserId);
    }

}