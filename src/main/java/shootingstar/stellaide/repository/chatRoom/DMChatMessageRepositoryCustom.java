package shootingstar.stellaide.repository.chatRoom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shootingstar.stellaide.repository.chatRoom.dto.FindAllDmMessageByRoomIdDTO;

import java.util.List;

public interface DMChatMessageRepositoryCustom {

    List<FindAllDmMessageByRoomIdDTO> findAllDMMessageById(Long roomId);

    Page<FindAllDmMessageByRoomIdDTO> findAllDMMessageById(Long roomId, Pageable pageable);
}
