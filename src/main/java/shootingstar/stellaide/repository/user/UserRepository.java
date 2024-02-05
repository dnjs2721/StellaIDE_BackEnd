package shootingstar.stellaide.repository.user;


import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.stellaide.entity.User;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, UserRepositoryCustom {
    boolean existsByEmail(String email);
    boolean existsByNickname(String email);
}
