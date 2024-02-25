package shootingstar.stellaide.controller.dto.userAuth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class LoginReqDto {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 5, max = 16)
    private String password;
}
