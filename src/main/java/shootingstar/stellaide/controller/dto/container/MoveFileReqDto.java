package shootingstar.stellaide.controller.dto.container;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MoveFileReqDto {
    @Size(min = 36, max = 36)
    private String containerId;
    @NotBlank
    private String currentPath;
    @NotBlank
    private String movedPath;
    @NotBlank
    private String fileName;
}
