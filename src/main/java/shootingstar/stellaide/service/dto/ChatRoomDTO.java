package shootingstar.stellaide.service.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
public class ChatRoomDTO {
    private Long roomId; //채팅방 아이디
    private String name; //채팅방 이름
    private Set<WebSocketSession> sessions = new HashSet<>();
    @Builder
    public ChatRoomDTO(String name, Long roomId) {
        this.roomId = roomId;
        this.name = name;
    }
}

