package shootingstar.stellaide.controller.dto.container;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EditContainerReqDto {
    @NotNull
    private String description;
}
