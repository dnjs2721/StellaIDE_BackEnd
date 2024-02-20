package shootingstar.stellaide.entity.chat;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shootingstar.stellaide.entity.user.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DirectChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dmChatRoomId;
    private String roomName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @OneToMany(mappedBy = "directChatRoom", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<DirectChatRoomMessage> messageList = new ArrayList<>();

    public DirectChatRoom(String roomName, User sender, User receiver){
        this.roomName = roomName;
        this.sender = sender;
        this.receiver = receiver;
    }
}
