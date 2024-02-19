package shootingstar.stellaide.entity.chat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class DMChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dmChatRoomId;

    private UUID sendId;
    private UUID reciveId;

    public DMChatRoom(UUID sendId, UUID reciveId){
        this.reciveId = reciveId;
        this.sendId = sendId;
    }

}
