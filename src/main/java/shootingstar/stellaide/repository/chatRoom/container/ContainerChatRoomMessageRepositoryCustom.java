package shootingstar.stellaide.repository.chatRoom.container;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shootingstar.stellaide.repository.chatRoom.dto.FindAllChatMessageByRoomIdDto;

import java.util.List;

public interface ContainerChatRoomMessageRepositoryCustom {
    List<FindAllChatMessageByRoomIdDto> findAllByRoomId(Long roomId);

    Page<FindAllChatMessageByRoomIdDto> findAllMessageById(Long roomId, Pageable pageable);
}
