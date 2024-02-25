package shootingstar.stellaide.controller.dto.container;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EditContainerReqDto {
    @NotEmpty
    @Size(min = 36, max = 36)
    private String containerId;
    @NotEmpty
    private String containerDescription;
}
