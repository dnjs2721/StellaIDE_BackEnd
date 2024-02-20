package shootingstar.stellaide.repository.chatRoom;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shootingstar.stellaide.entity.chat.ChatRoomMessage;

@Repository
public interface ChatRoomMessageRepository extends JpaRepository<ChatRoomMessage, Long> ,ChatRoomMessageRepositoryCustom{
}
