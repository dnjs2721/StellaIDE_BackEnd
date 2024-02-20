package shootingstar.stellaide.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shootingstar.stellaide.entity.chat.*;
import shootingstar.stellaide.entity.user.User;
import shootingstar.stellaide.exception.CustomException;
import shootingstar.stellaide.repository.chatRoom.container.ContainerChatRoomMessageRepository;
import shootingstar.stellaide.repository.chatRoom.container.ContainerChatRoomRepository;
import shootingstar.stellaide.repository.chatRoom.dm.DirectChatRoomMessageRepository;
import shootingstar.stellaide.repository.chatRoom.dm.DirectChatRoomRepository;
import shootingstar.stellaide.repository.chatRoom.dto.FindAllChatMessageByRoomIdDto;
import shootingstar.stellaide.repository.chatRoom.dto.FindAllDmMessageByRoomIdDto;
import shootingstar.stellaide.repository.chatRoom.global.GlobalChatRoomRepository;
import shootingstar.stellaide.repository.user.UserRepository;
import shootingstar.stellaide.service.dto.ChatRoomDto;
import shootingstar.stellaide.service.dto.ChatRoomMessageDto;

import java.util.*;

import static shootingstar.stellaide.exception.ErrorCode.NOT_FOUND_CHAT_ROOM;
import static shootingstar.stellaide.exception.ErrorCode.USER_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {
    private Map<Long, ChatRoomDto> containerChatRoomsDtoMap;
    private Map<Long, ChatRoomDto> dircetChatRoomDtoMap;
    private ChatRoomDto globalChatRoomDto;

    private final ContainerChatRoomRepository containerChatRoomRepository;
    private final ContainerChatRoomMessageRepository containerChatRoomMessageRepository;

    private final DirectChatRoomRepository directChatRoomRepository;
    private final DirectChatRoomMessageRepository directChatRoomMessageRepository;

    private final GlobalChatRoomRepository globalChatRoomRepository;

    private final UserRepository userRepository;

    @PostConstruct
    private void init() {
        containerChatRoomsDtoMap = new LinkedHashMap<>();
        dircetChatRoomDtoMap = new LinkedHashMap<>();
        GlobalChatRoom globalChatRoom = new GlobalChatRoom(999L);
        globalChatRoomRepository.save(globalChatRoom);
        globalChatRoomDto = ChatRoomDto.builder()
                .roomId(globalChatRoom.getGlobalChatRoomId())
                .name(globalChatRoom.getName())
                .build();
    }

    public ChatRoomDto findGlobalChatRoom() {
        return globalChatRoomDto;
    }

    public ChatRoomDto findContainerChatRoomById(Long chatRoomId) {
        if (containerChatRoomsDtoMap.containsKey(chatRoomId)) {
            return containerChatRoomsDtoMap.get(chatRoomId);
        }
        Optional<ContainerChatRoom> chatRoomOptional = containerChatRoomRepository.findById(chatRoomId);
        if (chatRoomOptional.isEmpty()) {
            throw new CustomException(NOT_FOUND_CHAT_ROOM);
        } else {
            ContainerChatRoom containerChatRoom = chatRoomOptional.get();
            ChatRoomDto chatRoomDTO = ChatRoomDto.builder()
                    .roomId(containerChatRoom.getChatRoomId())
                    .name(containerChatRoom.getChatRoomName())
                    .build();
            containerChatRoomsDtoMap.put(chatRoomId, chatRoomDTO);
            return chatRoomDTO;
        }
    }

    //컨테이너 메세지 저장
    @Transactional
    public void saveContainerMessage(ChatRoomMessageDto chatMessageDto, ChatRoomDto roomDto) {
        Optional<ContainerChatRoom> optionalChatRoom = containerChatRoomRepository.findById(roomDto.getRoomId());
        if(optionalChatRoom.isEmpty()){
            throw new CustomException(NOT_FOUND_CHAT_ROOM);
        }
        ContainerChatRoom containerChatRoom = optionalChatRoom.get();
        ContainerChatRoomMessage chatMessage = new ContainerChatRoomMessage(
                containerChatRoom,
                MessageType.TALK,
                chatMessageDto.getSender(),
                chatMessageDto.getMsg()
        );
        containerChatRoomMessageRepository.save(chatMessage);
    }

    //컨테이너 채팅 불러오기
    public Page<FindAllChatMessageByRoomIdDto> getAllMessagePage(Long roomId, Pageable pageable){
        return containerChatRoomMessageRepository.findAllMessageById(roomId, pageable);
    }

    /**
     * 디엠 채팅방 찾기
     */
    public ChatRoomDto findDmChatRoomById(Long chatRoomId) {
        if (dircetChatRoomDtoMap.containsKey(chatRoomId)) {
            return dircetChatRoomDtoMap.get(chatRoomId);
        }
        Optional<DirectChatRoom> chatRoomOptional = directChatRoomRepository.findById(chatRoomId);
        if (chatRoomOptional.isEmpty()) {
            throw new RuntimeException();
        } else {
            DirectChatRoom chatRoom = chatRoomOptional.get();
            ChatRoomDto chatRoomDTO = ChatRoomDto.builder()
                    .roomId(chatRoom.getDmChatRoomId())
                    .name(chatRoom.getRoomName())
                    .build();
            dircetChatRoomDtoMap.put(chatRoomId, chatRoomDTO);
            return chatRoomDTO;
        }
    }

    //dm 채팅방 생성
    @Transactional
    public void createDirectChatRoom(String sendId, String receiveId) {
        User sender = findUserByUUID(sendId);
        User receiver = findUserByUUID(receiveId);
        String roomName = sender.getNickname() + " & " + receiver.getNickname() + " Chat Room";
        DirectChatRoom directChatRoom = new DirectChatRoom(roomName,sendId,receiveId);
        directChatRoomRepository.save(directChatRoom);
    }
    //dm 메세지 저장
    @Transactional
    public void saveDirectMessage(ChatRoomMessageDto chatMessageDto, ChatRoomDto roomDto)  {
        Optional<DirectChatRoom> optionalDMChatRoom = directChatRoomRepository.findById(roomDto.getRoomId());
        if(optionalDMChatRoom.isEmpty()){
            throw new RuntimeException();
        }
        DirectChatRoom directChatRoom = optionalDMChatRoom.get();
        DirectChatRoomMessage directChatRoomMessage = new DirectChatRoomMessage(
                MessageType.TALK,
                directChatRoom,
                chatMessageDto.getSender(),
                chatMessageDto.getMsg());
        directChatRoomMessageRepository.save(directChatRoomMessage);
    }

    //디엠 채팅 불러오기
    public Page<FindAllDmMessageByRoomIdDto> getAllDMMessagePage(Long roomId, Pageable pageable){
        return directChatRoomMessageRepository.findAllDMMessageById(roomId,pageable);
    }

    /*
    채팅방 목록 불러오기
     */
    public List<DirectChatRoom> findAllRoom() {
        return directChatRoomRepository.findAll();
    }

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
}