package shootingstar.stellaide.repository.chatRoom.dm;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shootingstar.stellaide.repository.chatRoom.dto.FindAllDmMessageByRoomIdDto;

import java.util.List;

public interface DirectChatRoomMessageRepositoryCustom {

    List<FindAllDmMessageByRoomIdDto> findAllDMMessageById(Long roomId);

    Page<FindAllDmMessageByRoomIdDto> findAllDMMessageById(Long roomId, Pageable pageable);
}
