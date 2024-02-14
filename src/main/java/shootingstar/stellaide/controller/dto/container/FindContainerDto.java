package shootingstar.stellaide.controller.dto.container;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class FindContainerDto {
    private UUID containerId;
    private String type;
    private String name;
    private String description;
    private String path;
    private LocalDateTime createTime;
    private LocalDateTime editTime;
    private Long editUserId;

    @QueryProjection
    public FindContainerDto(UUID containerId, String type, String name, String description, String path, LocalDateTime createTime, LocalDateTime editTime, Long editUserId) {
        this.containerId = containerId;
        this.type = type;
        this.name = name;
        this.description = description;
        this.path = path;
        this.createTime = createTime;
        this.editTime = editTime;
        this.editUserId = editUserId;
    }
}
