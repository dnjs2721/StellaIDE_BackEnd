package shootingstar.stellaide.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import shootingstar.stellaide.controller.dto.container.FindContainerDto;
import shootingstar.stellaide.service.dto.ContainerTreeResDto;
import shootingstar.stellaide.service.dto.GetRoomResDto;
import shootingstar.stellaide.service.dto.SpringContainerResDto;
import shootingstar.stellaide.entity.chat.ChatRoom;
import shootingstar.stellaide.entity.container.Container;
import shootingstar.stellaide.entity.container.ContainerType;
import shootingstar.stellaide.entity.user.User;
import shootingstar.stellaide.exception.CustomException;
import shootingstar.stellaide.repository.chatRoom.ChatRoomRepository;
import shootingstar.stellaide.repository.container.ContainerRepository;
import shootingstar.stellaide.repository.user.UserRepository;
import shootingstar.stellaide.security.jwt.JwtTokenProvider;
import shootingstar.stellaide.util.SSHConnectionUtil;

import java.util.*;

import static shootingstar.stellaide.exception.ErrorCode.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContainerService {

    private final ContainerRepository containerRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    @Autowired
    private final SSHConnectionUtil sshConnectionUtil;
    private final ChatRoomRepository chatRoomRepository;

    public List<FindContainerDto> getContainer(String group, String query, String align) {
        return containerRepository.findContainer(group, query, align);
    }

    @Transactional
    public void createContainer(ContainerType type, String name, String description, String accessToken) {
        Authentication authentication = jwtTokenProvider.getAuthenticationFromAccessToken(accessToken);
        String userUuid = authentication.getName();

        User user = findUserByUUID(userUuid);

        if (user.getOwnedContainers().size() == 5) {
            throw new RuntimeException("최대 5개 까지만 생성가능");
        }

        // name 검증 로직 추가해야 함

        String containerName = user.getNickname() + "_" + name;
        if (containerRepository.existsByName(containerName)) {
            throw new RuntimeException("동일한 컨테이너 이름 존재");
        }
        sshConnectionUtil.createContainer(type, containerName);

        Container container = new Container(type, containerName, description, user);
        containerRepository.save(container);

        ChatRoom chatRoom = new ChatRoom(container, name+"Chat");
        chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public void deleteContainer(String containerId) {
        Container container = findContainerByUUID(containerId);

        sshConnectionUtil.deleteContainer(container.getName());
        containerRepository.delete(container);
    }

    public ContainerTreeResDto getTreeInfo(String containerId) {
        Container container = findContainerByUUID(containerId);
        String containerTree = sshConnectionUtil.getContainerTree(container.getName());
        return parseTextToDto(containerTree, container.getName());
    }

    public String getFileContent(String containerId, String filePath) {
        Container container = findContainerByUUID(containerId);
        return sshConnectionUtil.getFileContent(container.getName(), filePath);
    }

    public void createFile(String containerId, String filePath, String fileName) {
        Container container = findContainerByUUID(containerId);
        String containerName = container.getName();
        String path = containerName + filePath + fileName;
        sshConnectionUtil.createFile(path);
    }

    public void createDirectory(String containerId, String filePath, String directoryName) {
        Container container = findContainerByUUID(containerId);
        String containerName = container.getName();
        String path = containerName + filePath + directoryName;
        sshConnectionUtil.createDirectory(path);
    }

    public void executionFile(String containerId, String filePath) {
        Container container = findContainerByUUID(containerId);
        String containerName = container.getName();
        try {
            sshConnectionUtil.executionFile(containerName, filePath, container.getType());
        } catch (RuntimeException e) {
            log.info("실행 실패");
        }
    }

    public GetRoomResDto getRoomId(String containerId, String accessToken) {
        Authentication authentication = jwtTokenProvider.getAuthenticationFromAccessToken(accessToken);
        String userUuid = authentication.getName();
        User user = findUserByUUID(userUuid);
        Container container = findContainerByUUID(containerId);
        return new GetRoomResDto(user.getNickname(), container.getChatRoom().getChatRoomId());
    }

    public SpringContainerResDto executionSpring(String containerId) {
        Container container = findContainerByUUID(containerId);
        String containerName = container.getName();

        if (container.getType() != ContainerType.SPRING) {
            throw new RuntimeException();
        }

        String resUrl = null;

        try {
            resUrl = sshConnectionUtil.executionSpring(containerName);
        } catch (RuntimeException e) {
            log.info("실행 실패");
        }

        return new SpringContainerResDto(resUrl);
    }

    public void stopSpring(String containerId) {
        Container container = findContainerByUUID(containerId);
        String containerName = container.getName();

        if (container.getType() != ContainerType.SPRING) {
            throw new RuntimeException();
        }

        sshConnectionUtil.stopSpringContainer(containerName);
    }

        // accessToken 의 사용자 고유번호를 통해 사용자 검색
    private User findUserByUUID(String userUuid) {
        Optional<User> optionalUser = userRepository.findById(UUID.fromString(userUuid));
        if (optionalUser.isEmpty()) {
            // 엑세스 토큰을 통해 사용자를 찾지 못했을 때
            // 이 오류가 발생한다면 이미 탈퇴한 회원이 만료되지 않은 엑세스 토큰을 통해 비밀번호 확인을 시도했거나
            // 어떠한 방법으로 JWT 토큰의 사용자 고유번호를 변경했을 때
            throw new CustomException(USER_NOT_FOUND);
        }
        return optionalUser.get();
    }

    private Container findContainerByUUID(String containerId) {
        Optional<Container> optionalContainer = containerRepository.findById(UUID.fromString(containerId));
        if (optionalContainer.isEmpty()) {
            // 엑세스 토큰을 통해 사용자를 찾지 못했을 때
            // 이 오류가 발생한다면 이미 탈퇴한 회원이 만료되지 않은 엑세스 토큰을 통해 비밀번호 확인을 시도했거나
            // 어떠한 방법으로 JWT 토큰의 사용자 고유번호를 변경했을 때
            throw new RuntimeException("컨테이너 찾을 수 없음");
        }
        return optionalContainer.get();
    }

    private ContainerTreeResDto parseTextToDto(String text, String rootName) {
        String[] lines = text.split("\n");
        Map<String, ContainerTreeResDto> pathToDtoMap = new HashMap<>();
        ContainerTreeResDto root = new ContainerTreeResDto(rootName, "directory");
        pathToDtoMap.put(rootName, root); // 맵에 루트 디렉토리 추가

        for (int i = 0; i < lines.length - 1; i++) {
            String cleanLine = lines[i].trim();
            if (cleanLine.isEmpty()) continue;

            String permissions = cleanLine.substring(cleanLine.indexOf('[') + 1, cleanLine.indexOf(']')).trim();
            String type = permissions.startsWith("d") ? "directory" : "file";

            String path = cleanLine.substring(cleanLine.indexOf(']') + 2).trim();
            // 루트 디렉토리를 나타내는 경우, 추가 작업을 수행하지 않음
            if (path.equals(".")) continue;

            // 모든 경로가 ./로 시작하므로, 이를 rootName으로 대체
            path = path.replaceFirst("\\.", rootName);

            String name = path.substring(path.lastIndexOf('/') + 1);
            String parentPath = path.substring(0, path.lastIndexOf('/'));

            ContainerTreeResDto dto = new ContainerTreeResDto(name, type);
            // 부모를 찾지 못할경우 루트 경로로
            ContainerTreeResDto parentDto = pathToDtoMap.getOrDefault(parentPath, root);
            parentDto.addChild(dto);
            pathToDtoMap.put(path, dto);
        }

        return root;
    }
}
