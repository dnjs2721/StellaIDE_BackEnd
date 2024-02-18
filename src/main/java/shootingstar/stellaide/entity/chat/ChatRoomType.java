package shootingstar.stellaide.entity.chat;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ChatRoomType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;
    //roomId
    @Enumerated(EnumType.STRING)
    private RoomType roomType;
    // room type

    public ChatRoomType(RoomType type){
        this.roomType = type;
    }
}
