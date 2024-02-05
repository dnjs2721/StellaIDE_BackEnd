package shootingstar.stellaide.controller.dto.duplication;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckNicknameReqDto {
    @NotBlank
    private String nickname;
}
