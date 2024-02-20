package shootingstar.stellaide.entity.chat;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class DirectMiddleTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tableId;//테이블 아이디

    @OneToOne
    @JoinColumn(name = "user_Id")
    private UUID userId;//사용자 아이디


    private Long roomId;//사용자의 방아이디

    public DirectMiddleTable(UUID userId, Long roomId){
        this.userId = userId;
        this.roomId = roomId;
    }
}
