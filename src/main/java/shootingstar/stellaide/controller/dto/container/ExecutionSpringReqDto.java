package shootingstar.stellaide.controller.dto.container;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ExecutionSpringReqDto {
    @Size(min = 36, max = 36)
    private String containerId;
}
