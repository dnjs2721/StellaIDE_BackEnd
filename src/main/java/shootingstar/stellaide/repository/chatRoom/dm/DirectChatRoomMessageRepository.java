package shootingstar.stellaide.repository.chatRoom.dm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shootingstar.stellaide.entity.chat.DirectChatRoomMessage;

@Repository
public interface DirectChatRoomMessageRepository extends JpaRepository<DirectChatRoomMessage, Long>, DirectChatRoomMessageRepositoryCustom {
}
