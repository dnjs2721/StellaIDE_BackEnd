package shootingstar.stellaide.controller.dto.userAuth;

import lombok.Data;

@Data
public class DeleteUserReqDto {
    private String email;
    private String password;
}
