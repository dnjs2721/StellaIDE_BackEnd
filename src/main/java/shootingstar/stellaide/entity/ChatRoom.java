package shootingstar.stellaide.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomId;

    // 컨테이너 Id 외래키
    private Long containerId;
    private String name;

    @OneToMany(mappedBy = "chatRoom")
    private final List<ChatRoomMessage> messageList = new ArrayList<>();

    public ChatRoom(Long containerId, String name) {
        this.containerId = containerId;
        this.name = name;
    }

    public void addChatMessage(ChatRoomMessage message) {
        this.messageList.add(message);
    }
}
