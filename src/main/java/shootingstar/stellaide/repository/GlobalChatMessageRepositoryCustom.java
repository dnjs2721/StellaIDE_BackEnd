package shootingstar.stellaide.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GlobalChatMessageRepositoryCustom {
    List<FindAllGlobalByRoomIdDTO> findAllGlobalById(Long chatRoomId);

    Page<FindAllGlobalByRoomIdDTO> findAllGlobalById(Long chatRoomId, Pageable pageable);
}
