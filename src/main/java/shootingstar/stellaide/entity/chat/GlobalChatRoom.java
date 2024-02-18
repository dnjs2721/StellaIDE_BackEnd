package shootingstar.stellaide.entity.chat;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class GlobalChatRoom {
    @Id
    private Long globalChatRoomId;

    public GlobalChatRoom(Long globalChatRoomId){
        this.globalChatRoomId = globalChatRoomId;
    }
}
