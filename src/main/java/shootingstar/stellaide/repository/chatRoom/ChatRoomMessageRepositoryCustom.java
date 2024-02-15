package shootingstar.stellaide.repository.chatRoom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shootingstar.stellaide.repository.chatRoom.dto.FindAllChatMessageByRoomIdDTO;

import java.util.List;

public interface ChatRoomMessageRepositoryCustom {
    List<FindAllChatMessageByRoomIdDTO> findAllByRoomId(Long roomId);

    Page<FindAllChatMessageByRoomIdDTO> findAllMessageById(Long roomId, Pageable pageable);
}
