package shootingstar.stellaide.repository.chatRoom.container;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shootingstar.stellaide.entity.chat.ContainerChatRoom;

@Repository
public interface ContainerChatRoomRepository extends JpaRepository<ContainerChatRoom, Long> {
}
