package shootingstar.stellaide.controller.dto.userAuth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckPasswordReqDto {
    @NotBlank
    private String password;
}
