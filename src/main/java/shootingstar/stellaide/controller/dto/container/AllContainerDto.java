package shootingstar.stellaide.controller.dto.container;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import shootingstar.stellaide.entity.container.ContainerType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class AllContainerDto {
    private List<ContainerDto> ownContainers;
    private List<ContainerDto> shareContainers;

    public AllContainerDto(List<ContainerDto> ownContainers, List<ContainerDto> shareContainers) {
        this.ownContainers = ownContainers;
        this.shareContainers = shareContainers;
    }
}
