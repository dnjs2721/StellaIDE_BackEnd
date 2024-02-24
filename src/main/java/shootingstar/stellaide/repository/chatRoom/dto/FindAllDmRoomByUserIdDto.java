package shootingstar.stellaide.repository.chatRoom.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.util.UUID;

@Data
public class FindAllDmRoomByUserIdDto {
    private UUID userId;
    private Long roomId;

    @QueryProjection
    public FindAllDmRoomByUserIdDto(UUID userId, Long roomId){
        this.userId = userId;
        this.roomId = roomId;
    }
}
