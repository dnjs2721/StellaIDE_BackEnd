package shootingstar.stellaide.controller.dto.duplication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckEmailReqDto {
    @NotBlank
    @Email
    String email;
}
