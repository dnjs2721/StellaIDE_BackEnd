package shootingstar.stellaide.entity.chat;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shootingstar.stellaide.entity.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DirectChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dmChatRoomId;
    private String roomName;


    private UUID sender;
    private UUID receiver;

    @OneToMany(mappedBy = "directChatRoom", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<DirectChatRoomMessage> messageList = new ArrayList<>();

    @OneToMany(mappedBy = "directChatRoom", cascade = CascadeType.REMOVE)
    private final List<DirectMiddleTable> middleTable = new ArrayList<>();

    public DirectChatRoom(String roomName, UUID sender, UUID receiver){
        this.roomName = roomName;
        this.sender = sender;
        this.receiver = receiver;
    }
}
