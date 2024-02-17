package shootingstar.stellaide.service.dto;

import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

@Data
public class ChatRoomMessageDTO {

    private Long roomId; // 채팅방번호
    private String sender; // 메시지 보낸사람

    @NotNull
    private String msg; // 메시지

    @NotNull
    private MessageType type; // 메시지 타입

    public enum MessageType {
        ENTER, TALK
        //,QUIT
    }

    //    private ChatRoomDTO chatRoomDTO;
    public ChatRoomMessageDTO(MessageType type, Long roomId, String sender, String msg){
        this.type = type;
        this.roomId = roomId;
        this.sender = sender;
        this.msg = msg;

    }
}
