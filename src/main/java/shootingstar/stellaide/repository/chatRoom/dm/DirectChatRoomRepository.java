package shootingstar.stellaide.repository.chatRoom.dm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shootingstar.stellaide.entity.chat.DirectChatRoom;

@Repository
public interface DirectChatRoomRepository extends JpaRepository<DirectChatRoom,Long> {
}
