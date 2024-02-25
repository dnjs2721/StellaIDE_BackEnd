package shootingstar.stellaide.repository.user;


import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.stellaide.entity.user.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, UserRepositoryCustom {
    boolean existsByEmail(String email);
    boolean existsByNickname(String email);

    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String userNickname);
}
