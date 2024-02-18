package shootingstar.stellaide.repository.chatRoom;

import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.stellaide.entity.chat.ChatRoomMessage;
import shootingstar.stellaide.entity.chat.ChatRoomType;

public interface ChatRoomTypeRepository extends JpaRepository<ChatRoomType, Long>{
}
