package shootingstar.stellaide.entity.chat;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shootingstar.stellaide.entity.container.Container;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomId;

    private String chatRoomName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "container_id")
    private Container container;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<ChatRoomMessage> messageList = new ArrayList<>();

    public ChatRoom(Container container, String name) {
        this.container = container;
        this.chatRoomName = name;
    }

    public void addChatMessage(ChatRoomMessage message) {
        this.messageList.add(message);
    }
}
