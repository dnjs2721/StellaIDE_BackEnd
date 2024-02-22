package shootingstar.stellaide.controller.dto.container;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SaveFileReqDto {
    @Size(min = 36, max = 36)
    private String containerId;
    @NotBlank
    private String path;
    @NotBlank
    private String fileName;
    @NotNull
    private String fileContent;
}
