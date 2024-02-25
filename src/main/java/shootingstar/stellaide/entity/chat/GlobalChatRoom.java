package shootingstar.stellaide.entity.chat;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GlobalChatRoom {
    @Id
    private Long globalChatRoomId;
    private String name;

    public GlobalChatRoom(Long globalChatRoomId){
        this.globalChatRoomId = globalChatRoomId;
        this.name = "Global Chat Room";
    }
}
