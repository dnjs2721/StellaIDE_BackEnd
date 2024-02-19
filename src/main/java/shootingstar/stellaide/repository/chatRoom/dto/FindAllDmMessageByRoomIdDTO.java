package shootingstar.stellaide.repository.chatRoom.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import shootingstar.stellaide.entity.chat.MessageType;

import java.time.LocalDateTime;

@Data
public class FindAllDmMessageByRoomIdDTO {
    private String sender;
    private String msg;
    private LocalDateTime createTime ;
    private Long roomId;

    @QueryProjection
    public FindAllDmMessageByRoomIdDTO(Long roomId, String sender, String msg, LocalDateTime createTime){
        this.roomId = roomId;
        this.sender = sender;
        this.msg = msg;
        this.createTime = createTime;
    }

}
