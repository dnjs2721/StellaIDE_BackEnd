package shootingstar.stellaide.controller.dto.emailVerification;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VerificationEmailReqDto {
    @NotBlank
    @Email
    String email;

    @NotBlank
    @Size(min = 8, max = 8)
    String code;
}
