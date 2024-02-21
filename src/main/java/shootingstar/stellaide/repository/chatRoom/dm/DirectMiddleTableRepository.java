package shootingstar.stellaide.repository.chatRoom.dm;

import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.stellaide.entity.chat.DirectChatRoom;
import shootingstar.stellaide.entity.chat.DirectMiddleTable;

import java.util.List;
import java.util.UUID;

public interface DirectMiddleTableRepository extends JpaRepository<DirectMiddleTable,Long>,DirectMiddleTableRepositoryCustom {
  //  List<DirectMiddleTable> findByDirectChatRoom_DmChatId(UUID userId);
}
