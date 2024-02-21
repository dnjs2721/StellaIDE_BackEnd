package shootingstar.stellaide.entity.chat;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shootingstar.stellaide.entity.user.User;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class DirectMiddleTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tableId;//테이블 아이디

    @ManyToOne
    @JoinColumn(name = "dm_User_Id")
    private User user;//사용자 아이디

    @ManyToOne
    @JoinColumn(name = "dm_chat_id")
    private DirectChatRoom directChatRoom; //사용자의 방아이디

    private String name;

    public DirectMiddleTable(User user, DirectChatRoom directChatRoom){
        this.user = user;
        this.directChatRoom = directChatRoom;
        this.name = directChatRoom.getRoomName();
    }
}
