package shootingstar.stellaide.service.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ChatRoomDto {
    private Long roomId; //채팅방 아이디
    private String name; //채팅방 이름
    @Builder
    public ChatRoomDto(String name, Long roomId) {
        this.roomId = roomId;
        this.name = name;
    }
}

