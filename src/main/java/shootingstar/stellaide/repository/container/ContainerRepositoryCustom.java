package shootingstar.stellaide.repository.container;

import shootingstar.stellaide.controller.dto.container.FindContainerDto;

import java.util.List;

public interface ContainerRepositoryCustom {
    List<FindContainerDto> findContainer(String group, String query, String align);
}
