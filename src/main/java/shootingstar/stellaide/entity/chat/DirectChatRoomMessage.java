package shootingstar.stellaide.entity.chat;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shootingstar.stellaide.entity.BaseTimeTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DirectChatRoomMessage extends BaseTimeTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dmChatRoomId")
    private DirectChatRoom directChatRoom;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @NotBlank
    private String sender;

    @NotBlank
    @Column(columnDefinition="LONGTEXT")
    private String message;

    public DirectChatRoomMessage(MessageType messageType, DirectChatRoom directChatRoom, String sender, String message) {
        this.messageType = messageType;
        this.directChatRoom = directChatRoom;
        this.sender = sender;
        this.message = message;
    }
}
