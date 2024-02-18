package shootingstar.stellaide.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import shootingstar.stellaide.controller.dto.container.FindContainerDto;
import shootingstar.stellaide.entity.chat.ChatRoom;
import shootingstar.stellaide.entity.chat.ChatRoomType;
import shootingstar.stellaide.entity.chat.RoomType;
import shootingstar.stellaide.entity.container.Container;
import shootingstar.stellaide.entity.container.ContainerType;
import shootingstar.stellaide.entity.user.User;
import shootingstar.stellaide.exception.CustomException;
import shootingstar.stellaide.repository.chatRoom.ChatRoomRepository;
import shootingstar.stellaide.repository.chatRoom.ChatRoomTypeRepository;
import shootingstar.stellaide.repository.container.ContainerRepository;
import shootingstar.stellaide.repository.user.UserRepository;
import shootingstar.stellaide.security.jwt.JwtTokenProvider;
import shootingstar.stellaide.util.SSHConnectionUtil;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static shootingstar.stellaide.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ContainerService {

    private final ContainerRepository containerRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final SSHConnectionUtil sshConnectionUtil;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomTypeRepository chatRoomTypeRepository;

    public List<FindContainerDto> getContainer(String group, String query, String align) {
        return containerRepository.findContainer(group, query, align);
    }

    @Transactional
    public void createContainer(ContainerType type, String name, String description, String accessToken) {
        Authentication authentication = jwtTokenProvider.getAuthenticationFromAccessToken(accessToken);
        String userUuid = authentication.getName();

        User user = findUserByUUID(userUuid);

        if (user.getOwnedContainers().size() == 5) {
            throw new RuntimeException("최대 5개  까지만 생성가능");
        }

        // name 검증 로직 추가해야 함

        String containerName = user.getNickname() + "_" + name;
        if (containerRepository.existsByName(containerName)) {
            throw new RuntimeException("동일한 컨테이너 이름 존재");
        }
        sshConnectionUtil.createContainer(type, containerName);

        Container container = new Container(type, containerName, description, user);
        containerRepository.save(container);

        ChatRoomType chatRoomType = new ChatRoomType(RoomType.CONTAINER);
        chatRoomTypeRepository.save(chatRoomType);
        ChatRoom chatRoom = new ChatRoom(container, name + " Chat");
        chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public void deleteContainer(String containerId) {
        Container container = findContainerByUUID(containerId);

        sshConnectionUtil.deleteContainer(container.getName());
        containerRepository.delete(container);
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
}
