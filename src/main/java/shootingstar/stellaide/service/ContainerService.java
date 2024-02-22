package shootingstar.stellaide.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import shootingstar.stellaide.controller.dto.container.AllContainerDto;
import shootingstar.stellaide.controller.dto.container.ContainerDto;
import shootingstar.stellaide.entity.chat.ContainerChatRoom;
import shootingstar.stellaide.service.dto.ContainerTreeResDto;
import shootingstar.stellaide.service.dto.GetRoomResDto;
import shootingstar.stellaide.service.dto.SpringContainerResDto;
import shootingstar.stellaide.entity.SharedUserContainer;
import shootingstar.stellaide.entity.container.Container;
import shootingstar.stellaide.entity.container.ContainerType;
import shootingstar.stellaide.entity.user.User;
import shootingstar.stellaide.exception.CustomException;
import shootingstar.stellaide.repository.chatRoom.container.ContainerChatRoomRepository;
import shootingstar.stellaide.repository.container.ContainerRepository;
import shootingstar.stellaide.repository.sharedUserContainer.SharedUserContainerRepository;
import shootingstar.stellaide.repository.user.UserRepository;
import shootingstar.stellaide.security.jwt.JwtTokenProvider;
import shootingstar.stellaide.util.SSHConnectionUtil;

import java.util.*;

import static shootingstar.stellaide.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContainerService {

    private final JwtTokenProvider jwtTokenProvider;
    private final SSHConnectionUtil sshConnectionUtil;
    private final CheckDuplicateService duplicateService;

    private final UserRepository userRepository;
    private final ContainerRepository containerRepository;
    private final ContainerChatRoomRepository containerChatRoomRepository;
    private final SharedUserContainerRepository sharedUserContainerRepository;

    public AllContainerDto getContainer(String accessToken) {
        Authentication authentication = jwtTokenProvider.getAuthenticationFromAccessToken(accessToken);
        String userUuid = authentication.getName();

        User user = findUserByUUID(userUuid);

        return containerRepository.findContainer(user.getUserId());
    }

    @Transactional
    public ContainerDto createContainer(ContainerType type, String name, String description, String accessToken) {
        Authentication authentication = jwtTokenProvider.getAuthenticationFromAccessToken(accessToken);
        String userUuid = authentication.getName();

        User user = findUserByUUID(userUuid);

        if (user.getOwnedContainers().size() == 5) {
            throw new CustomException(MAX_CONTAINER_ERROR);
        }

        String containerName = user.getNickname() + "_" + name;
        duplicateService.checkDuplicateContainerName(containerName); // 컨테이너 이름 검사

        sshConnectionUtil.createContainer(type, containerName);

        Container container = new Container(type, containerName, description, user);
        containerRepository.save(container);

        ContainerChatRoom containerChatRoom = new ContainerChatRoom(container, name + " Chat");
        containerChatRoomRepository.save(containerChatRoom);

        return new ContainerDto(
                container.getContainerId(),
                container.getType(),
                container.getName(),
                container.getDescription(),
                container.getCreatedTime(),
                container.getLastModifiedTime(),
                container.getEditUserNickname());
    }

    @Transactional
    public void editContainer(String containerId, String description, String accessToken) {
        Authentication authentication = jwtTokenProvider.getAuthenticationFromAccessToken(accessToken);
        Container container = findContainerByUUID(containerId);

        String userUuid = authentication.getName();
        User user = findUserByUUID(userUuid);

        checkPermissionIncludeShared(user, container);

        container.changeDescription(description);
    }

    @Transactional
    public void deleteContainer(String containerId, String accessToken) {
        Authentication authentication = jwtTokenProvider.getAuthenticationFromAccessToken(accessToken);
        Container container = findContainerByUUID(containerId);

        String userUuid = authentication.getName();
        User user = findUserByUUID(userUuid);

        checkOwnerPermission(user, container);

        containerRepository.delete(container);
        sshConnectionUtil.deleteContainer(container.getName());
    }



    @Transactional
    public void shareContainer(String containerId, String userNickname, String accessToken) {
        Authentication authentication = jwtTokenProvider.getAuthenticationFromAccessToken(accessToken);
        Container container = findContainerByUUID(containerId);

        String userUuid = authentication.getName();
        User user = findUserByUUID(userUuid);

        if (user.getNickname().equals(userNickname)) {
            throw new CustomException(FAILED_SHARED_ERROR);
        }

        checkOwnerPermission(user, container);

        List<SharedUserContainer> sharedUsers = container.getSharedUsers();
        if (sharedUsers.size() == 5) {
            throw new CustomException(MAX_SHARED_ERROR);
        }

        Optional<User> optionalUser = userRepository.findByNickname(userNickname);
        if (optionalUser.isEmpty()) {
            throw new CustomException(USER_NOT_FOUND);
        }
        User shareUser = optionalUser.get();

        for (SharedUserContainer sharedUserContainer : sharedUsers) {
            if (sharedUserContainer.getSharedUser().equals(shareUser)) {
                throw new CustomException(ALREADY_SHARED_ERROR);
            }
        }

        SharedUserContainer sharedUserContainer = new SharedUserContainer(container, shareUser);
        sharedUserContainerRepository.save(sharedUserContainer);
    }

    @Transactional
    public void cancelContainerSharing(String containerId, String userNickname, String accessToken) {
        Authentication authentication = jwtTokenProvider.getAuthenticationFromAccessToken(accessToken);
        Container container = findContainerByUUID(containerId);

        String userUuid = authentication.getName();
        User user = findUserByUUID(userUuid);

        checkPermissionIncludeShared(user, container);
        boolean owner = container.getOwner().equals(user);

        Optional<User> optionalUser = userRepository.findByNickname(userNickname);
        if (optionalUser.isEmpty()) {
            throw new CustomException(USER_NOT_FOUND);
        }
        User shareUser = optionalUser.get();

        Optional<SharedUserContainer>  optionalSharedUserContainer= sharedUserContainerRepository.findByContainerAndSharedUser(container, shareUser);
        if (optionalSharedUserContainer.isEmpty()) {
            throw new CustomException(NOT_FOUND_SHARED_USER_CONTAINER);
        }
        SharedUserContainer sharedUserContainer = optionalSharedUserContainer.get();

        if (shareUser.equals(user)) {
            sharedUserContainerRepository.delete(sharedUserContainer);
        } else {
            if (owner) {
                sharedUserContainerRepository.delete(sharedUserContainer);
            } else {
                throw new CustomException(DENIED_PERMISSION_CONTAINER);
            }
        }
    }

    public String getContainerType(String containerId) {
        Container container = findContainerByUUID(containerId);
        return container.getType().toString();
    }

    public GetRoomResDto getRoomId(String containerId, String accessToken) {
        Authentication authentication = jwtTokenProvider.getAuthenticationFromAccessToken(accessToken);
        String userUuid = authentication.getName();
        User user = findUserByUUID(userUuid);

        Container container = findContainerByUUID(containerId);
        checkPermissionIncludeShared(user, container);

        return new GetRoomResDto(user.getNickname(), container.getContainerChatRoom().getChatRoomId(), container.getName());
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
        String path = containerName + filePath;
        sshConnectionUtil.createFile(path, fileName);
    }

    public void createDirectory(String containerId, String filePath, String directoryName) {
        Container container = findContainerByUUID(containerId);
        String containerName = container.getName();
        String path = containerName + filePath + directoryName;
        sshConnectionUtil.createDirectory(path);
    }

    public String executionFile(String containerId, String filePath) {
        Container container = findContainerByUUID(containerId);

        if (!(container.getType() == ContainerType.JAVA || container.getType() == ContainerType.PYTHON)) {
            throw new CustomException(NOT_SUPPORT_CONTAINER_TYPE);
        }

        String containerName = container.getName();
        return sshConnectionUtil.executionFile(containerName, filePath, container.getType());
    }

    public SpringContainerResDto executionSpring(String containerId) {
        Container container = findContainerByUUID(containerId);
        String containerName = container.getName();

        if (container.getType() != ContainerType.SPRING) {
            throw new RuntimeException();
        }

        String resUrl = null;
        resUrl = sshConnectionUtil.executionSpring(containerName);

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
            throw new CustomException(NOT_FOUND_CONTAINER);
        }
        return optionalContainer.get();
    }

    private static void checkOwnerPermission(User user, Container container) {
        if (!user.getUserId().equals(container.getOwner().getUserId())) {
            throw new CustomException(DENIED_PERMISSION_CONTAINER);
        }
    }

    private static void checkPermissionIncludeShared(User user, Container container) {
        List<SharedUserContainer> sharedUsers = container.getSharedUsers();
        if (!user.getUserId().equals(container.getOwner().getUserId())) {
            boolean flag = true;
            for (SharedUserContainer sharedUser : sharedUsers) {
                if (sharedUser.getSharedUser().equals(user)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                throw new CustomException(DENIED_PERMISSION_CONTAINER);
            }
        }
    }

    private ContainerTreeResDto parseTextToDto(String text, String rootName) {
        try {
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
        } catch (Exception e) {
            throw new CustomException(FAILED_LOAD_CONTAINER_TREE);
        }
    }
}
