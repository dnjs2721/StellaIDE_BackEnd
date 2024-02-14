package shootingstar.stellaide.repository.container;

import shootingstar.stellaide.controller.dto.container.FindContainerDto;
import shootingstar.stellaide.controller.dto.container.SaveContainerDto;

import java.util.List;
import java.util.UUID;

public interface ContainerRepositoryCustom {
    List<FindContainerDto> findContainer(String group, String query, String align);

    //void insertContainer(SaveContainerDto saveContainerDto);

    //void deleteById(UUID containerId);


}
