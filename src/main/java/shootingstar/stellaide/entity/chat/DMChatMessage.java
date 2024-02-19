package shootingstar.stellaide.entity.chat;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DMChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dmChatRoomId")
    private DMChatRoom dmChatRoom;

    @NotBlank
    private String sender;
    @NotBlank
    private String message;
    private LocalDateTime createTime;


    public DMChatMessage(DMChatRoom dmChatRoom, String sender, String message) {
        this.dmChatRoom = dmChatRoom;
        this.sender = sender;
        this.message = message;
        this.createTime = LocalDateTime.now();
    }
}
