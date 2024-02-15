package shootingstar.stellaide.repository.sharedUserContainer;


import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.stellaide.entity.SharedUserContainer;

import java.util.UUID;

public interface SharedUserContainerRepository extends JpaRepository<SharedUserContainer, UUID> {
}
