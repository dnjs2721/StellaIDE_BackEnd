package shootingstar.stellaide.repository.container;

import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.stellaide.entity.Container;

import java.util.UUID;

public interface ContainerRepository extends JpaRepository<Container, UUID>, ContainerRepositoryCustom {
}
