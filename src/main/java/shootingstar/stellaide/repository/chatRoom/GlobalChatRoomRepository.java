package shootingstar.stellaide.repository.chatRoom;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shootingstar.stellaide.entity.chat.GlobalChatRoom;

@Repository
public interface GlobalChatRoomRepository extends JpaRepository<GlobalChatRoom, Long> {
}
