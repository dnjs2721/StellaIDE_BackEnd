package shootingstar.stellaide.service.dto;

import lombok.Data;
import shootingstar.stellaide.entity.chat.ChatRoomType;
import shootingstar.stellaide.entity.chat.MessageType;

@Data
public class ChatRoomMessageDto {

    private Long roomId; // 채팅방번호
    private String sender; // 메시지 보낸사람
    private String msg; // 메시지
    private MessageType type; // 메시지 타입
    private ChatRoomType roomType;

    public ChatRoomMessageDto(MessageType type, Long roomId, String sender, String msg, ChatRoomType roomType){
        this.type = type;
        this.roomId = roomId;
        this.sender = sender;
        this.msg = msg;
        this.roomType = roomType;
    }
}
