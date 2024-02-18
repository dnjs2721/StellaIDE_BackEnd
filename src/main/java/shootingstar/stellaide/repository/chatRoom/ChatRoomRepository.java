package shootingstar.stellaide.repository.chatRoom;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shootingstar.stellaide.entity.chat.ChatRoom;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}
