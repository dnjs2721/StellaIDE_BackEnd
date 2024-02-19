package shootingstar.stellaide.repository.container;

import shootingstar.stellaide.controller.dto.container.AllContainerDto;

import java.util.List;
import java.util.UUID;

public interface ContainerRepositoryCustom {
    AllContainerDto findContainer(UUID userUuid);
}
