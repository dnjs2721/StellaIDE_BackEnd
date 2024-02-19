package shootingstar.stellaide.repository.container;

import shootingstar.stellaide.controller.dto.container.FindContainerDto;
import shootingstar.stellaide.entity.container.ContainerAlign;
import shootingstar.stellaide.entity.container.ContainerGroup;

import java.util.List;
import java.util.UUID;

public interface ContainerRepositoryCustom {
    List<FindContainerDto> findContainer(ContainerGroup group, String query, ContainerAlign align, UUID userUuid);
}
