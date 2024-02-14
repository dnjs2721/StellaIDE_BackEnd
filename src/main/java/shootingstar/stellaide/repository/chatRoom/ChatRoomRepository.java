package shootingstar.stellaide.repository.chatRoom;

import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.stellaide.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}
