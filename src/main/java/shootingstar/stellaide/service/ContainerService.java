package shootingstar.stellaide.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shootingstar.stellaide.controller.dto.container.FindContainerDto;
import shootingstar.stellaide.controller.dto.container.SaveContainerDto;
import shootingstar.stellaide.entity.Container;
import shootingstar.stellaide.repository.container.ContainerRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContainerService {

    private final ContainerRepository containerRepository;

    public List<FindContainerDto> getContainer(String group, String query, String align) {
        return containerRepository.findContainer(group, query, align);
    }

    @Transactional
    public void create(SaveContainerDto saveContainerDto) {
        String cmd = "mkdir " + saveContainerDto.getName();

        String path = "/bin/sh/" + saveContainerDto.getName();
        Long editUserId = 1L;

        Container container = new Container(
                saveContainerDto.getType(),
                saveContainerDto.getName(),
                saveContainerDto.getDescription(),
                path,
                editUserId
        );

        containerRepository.save(container);

    }

    @Transactional
    public void delete(UUID containerId) {

        // 1. 데이터베이스에서 제거
        // 2. 리눅스 디렉토리 제거

        containerRepository.deleteById(containerId);
    }
}
