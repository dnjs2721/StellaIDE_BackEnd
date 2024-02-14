package shootingstar.stellaide.repository.chatRoom;

import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.stellaide.entity.ChatRoom;
import shootingstar.stellaide.entity.ChatRoomMessage;

public interface ChatRoomMessageRepository extends JpaRepository<ChatRoomMessage, Long> {
}
