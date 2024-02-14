package shootingstar.stellaide.controller.dto.container;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SaveContainerDto {
    @NotBlank
    private String type;

    @NotBlank
    private String name;

    @NotBlank
    private String description;


}
