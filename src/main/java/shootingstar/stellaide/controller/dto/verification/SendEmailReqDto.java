package shootingstar.stellaide.controller.dto.verification;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendEmailReqDto {
    @NotBlank
    @Email
    String email;
}
