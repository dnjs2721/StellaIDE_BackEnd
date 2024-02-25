package shootingstar.stellaide.controller.dto.container;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import shootingstar.stellaide.entity.container.ContainerType;

@Data
public class CreateContainerReqDto {
    @NotNull
    private ContainerType containerType;
    @NotBlank
    private String containerName;
    @NotBlank
    private String containerDescription;
}
