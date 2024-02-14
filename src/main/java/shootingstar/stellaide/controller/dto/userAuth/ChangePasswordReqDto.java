package shootingstar.stellaide.controller.dto.userAuth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordReqDto {
    @NotBlank
    @Size(min = 8, max = 16)
    private String password;

    @NotBlank
    @Size(min = 8, max = 16)
    private String newPassword;
}
