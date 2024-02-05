package shootingstar.stellaide.controller.dto.verification;

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
    @Size(min = 8, max = 8, message = "잘못된 인증 코드입니다.")
    String code;
}
