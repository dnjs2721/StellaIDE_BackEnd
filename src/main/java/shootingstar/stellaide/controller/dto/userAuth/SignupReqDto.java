package shootingstar.stellaide.controller.dto.userAuth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupReqDto {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Size(min = 8, max = 16)
    private String password;
    @NotBlank
    @Size(min = 5, max = 20)
    private String nickname;
}
