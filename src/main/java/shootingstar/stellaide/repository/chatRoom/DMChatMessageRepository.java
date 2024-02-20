package shootingstar.stellaide.repository.chatRoom;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shootingstar.stellaide.entity.chat.DMChatMessage;

@Repository
public interface DMChatMessageRepository extends JpaRepository<DMChatMessage, Long>, DMChatMessageRepositoryCustom {
}
