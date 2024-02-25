package shootingstar.stellaide.repository.sharedUserContainer;


import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.stellaide.entity.SharedUserContainer;
import shootingstar.stellaide.entity.container.Container;
import shootingstar.stellaide.entity.user.User;

import java.util.Optional;
import java.util.UUID;

public interface SharedUserContainerRepository extends JpaRepository<SharedUserContainer, UUID> {
    Optional<SharedUserContainer> findByContainerAndSharedUser(Container container, User user);
}
