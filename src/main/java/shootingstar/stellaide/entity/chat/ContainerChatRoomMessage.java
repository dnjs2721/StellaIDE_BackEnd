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
public class ContainerChatRoomMessage extends BaseTimeTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatRoomId")
    private ContainerChatRoom containerChatRoom;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @NotBlank
    private String sender;
    @NotBlank
    @Column(columnDefinition="LONGTEXT")
    private String message;

    public ContainerChatRoomMessage(ContainerChatRoom containerChatRoom, MessageType messageType, String sender, String message) {
        this.containerChatRoom = containerChatRoom;
        this.messageType = messageType;
        this.sender = sender;
        this.message = message;
    }
}
