package shootingstar.stellaide.controller.dto.userAuth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordReqDto {
    @NotBlank
    private String password;

    @NotBlank
    private String newPassword;
}
