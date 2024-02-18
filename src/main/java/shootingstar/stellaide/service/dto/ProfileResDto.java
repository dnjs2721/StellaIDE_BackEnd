package shootingstar.stellaide.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfileResDto {
    private String email;
    private String nickname;
    private String profileImgUrl;
}
