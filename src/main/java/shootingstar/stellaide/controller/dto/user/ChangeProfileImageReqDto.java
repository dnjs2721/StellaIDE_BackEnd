package shootingstar.stellaide.controller.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ChangeProfileImageReqDto {
    @NotNull
    private MultipartFile profileImgFile;
}
