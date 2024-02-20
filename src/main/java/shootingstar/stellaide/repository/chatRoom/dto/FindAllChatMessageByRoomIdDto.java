package shootingstar.stellaide.repository.chatRoom.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import shootingstar.stellaide.entity.chat.MessageType;

import java.time.LocalDateTime;

@Data
public class FindAllChatMessageByRoomIdDto {
    private String sender;
    private String msg;
    private MessageType type;
    private LocalDateTime createTime ;
    private Long roomId;

    @QueryProjection
    public FindAllChatMessageByRoomIdDto(Long roomId, String sender, String msg, LocalDateTime createTime, MessageType type){
        this.roomId = roomId;
        this.sender = sender;
        this.msg = msg;
        this.type = type;
        this.createTime = createTime;
    }
}
