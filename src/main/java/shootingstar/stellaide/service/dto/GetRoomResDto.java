package shootingstar.stellaide.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetRoomResDto {
    private String nickname;
    private Long roomId;
    private String containerName;
}
