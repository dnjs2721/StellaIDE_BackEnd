package shootingstar.stellaide.controller.dto.container;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import shootingstar.stellaide.entity.container.ContainerType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class FindContainerDto {
    private UUID containerId;
    private ContainerType type;
    private String name;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime editTime;
    private UUID editUserUuid;

    @QueryProjection
    public FindContainerDto(UUID containerId, ContainerType type, String name, String description, LocalDateTime createTime, LocalDateTime editTime, UUID editUserUuid) {
        this.containerId = containerId;
        this.type = type;
        this.name = name;
        this.description = description;
        this.createTime = createTime;
        this.editTime = editTime;
        this.editUserUuid = editUserUuid;
    }
}
