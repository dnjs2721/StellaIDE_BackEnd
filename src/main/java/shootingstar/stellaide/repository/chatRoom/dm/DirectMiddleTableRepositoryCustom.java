package shootingstar.stellaide.repository.chatRoom.dm;

import shootingstar.stellaide.repository.chatRoom.dto.FindAllDmRoomByUserIdDto;

import java.util.List;
import java.util.UUID;

public interface DirectMiddleTableRepositoryCustom {
    List<FindAllDmRoomByUserIdDto>  findAllByUserId(UUID userId);
}
