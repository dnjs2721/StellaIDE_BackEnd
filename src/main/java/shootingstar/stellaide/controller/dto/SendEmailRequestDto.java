package shootingstar.stellaide.controller.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class SendEmailRequestDto {
    @Email
    String email;
}
