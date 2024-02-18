package shootingstar.stellaide.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.stellaide.entity.chat.GlobalChatRoom;

public interface GlobalChatRoomRepository extends JpaRepository <GlobalChatRoom, Long> {
}
