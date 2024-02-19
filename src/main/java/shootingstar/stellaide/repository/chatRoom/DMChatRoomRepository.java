package shootingstar.stellaide.repository.chatRoom;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shootingstar.stellaide.entity.chat.DMChatRoom;

@Repository
public interface DMChatRoomRepository extends JpaRepository<DMChatRoom,Long> {
}
