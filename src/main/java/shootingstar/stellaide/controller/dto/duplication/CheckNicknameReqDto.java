package shootingstar.stellaide.controller.dto.duplication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CheckNicknameReqDto {
    @NotBlank
    @Size(min = 5, max = 20)
    private String nickname;
}
