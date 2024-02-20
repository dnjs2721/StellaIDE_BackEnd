package shootingstar.stellaide.repository.chatRoom.container;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shootingstar.stellaide.entity.chat.ContainerChatRoomMessage;

@Repository
public interface ContainerChatRoomMessageRepository extends JpaRepository<ContainerChatRoomMessage, Long> , ContainerChatRoomMessageRepositoryCustom {
}
