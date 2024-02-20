package shootingstar.stellaide.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shootingstar.stellaide.entity.chat.*;
import shootingstar.stellaide.repository.chatRoom.*;
import shootingstar.stellaide.repository.chatRoom.dto.FindAllChatMessageByRoomIdDTO;
import shootingstar.stellaide.repository.chatRoom.dto.FindAllDmMessageByRoomIdDTO;
import shootingstar.stellaide.service.dto.ChatRoomDTO;
import shootingstar.stellaide.service.dto.ChatRoomMessageDTO;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    private final ObjectMapper objectMapper;
    private Map<Long, ChatRoomDTO> chatRoomsDTO;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMessageRepository chatMessageRepository;
    private final GlobalChatRoomRepository globalChatRoomRepository;
    private final DMChatRoomRepository dmChatRoomRepository;
    private final DMChatMessageRepository dmChatMessageRepository;
    private Map<String, ChatRoomMessageDTO> chatRoomMessageDTO;

    @PostConstruct
    private void init() {
        chatRoomsDTO = new LinkedHashMap<>();
        GlobalChatRoom globalChatRoom = new GlobalChatRoom(999L);
        globalChatRoomRepository.save(globalChatRoom);
    }


    /*
    채팅방 목록 불러오기
     */
    public List<DMChatRoom> findAllRoom() {
        return dmChatRoomRepository.findAll();
    }

    /**
     * 글로벌 채팅 찾기
     * @param chatRoomId
     * @return
     */
    public ChatRoomDTO findRoomById(Long chatRoomId) {
        if(chatRoomId==999L) {
            if (chatRoomsDTO.containsKey(chatRoomId)) {
                return chatRoomsDTO.get(chatRoomId);
            }
            Optional<GlobalChatRoom> globalChatRoomOptional = globalChatRoomRepository.findById(chatRoomId);
            if (globalChatRoomOptional.isEmpty()) {
                throw new RuntimeException();
            } else {
                GlobalChatRoom globalChatRoom = globalChatRoomOptional.get();
                ChatRoomDTO chatRoomDTO = ChatRoomDTO.builder()
                        .roomId(globalChatRoom.getGlobalChatRoomId())
                        .name("globalChatRoom")
                        .build();
                chatRoomsDTO.put(chatRoomId, chatRoomDTO);
                return chatRoomDTO;
            }
        }
        else{
            if (chatRoomsDTO.containsKey(chatRoomId)) {
                return chatRoomsDTO.get(chatRoomId);
            }
            Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(chatRoomId);
            if (chatRoomOptional.isEmpty()) {
                throw new RuntimeException();
            } else {
                ChatRoom chatRoom = chatRoomOptional.get();
                ChatRoomDTO chatRoomDTO = ChatRoomDTO.builder()
                        .roomId(chatRoom.getChatRoomId())
                        .name(chatRoom.getChatRoomName())
                        .build();
                chatRoomsDTO.put(chatRoomId, chatRoomDTO);
                return chatRoomDTO;
            }
        }
    }

    /**
     * 디엠 채팅방 찾기
     * @param dmChatRoomId
     * @return
     */
    public ChatRoomDTO findDMRoomById(Long dmChatRoomId) {
        if (chatRoomsDTO.containsKey(dmChatRoomId)) {
            return chatRoomsDTO.get(dmChatRoomId);
        }
        Optional<DMChatRoom> dmChatRoomOptional = dmChatRoomRepository.findById(dmChatRoomId);
        if (dmChatRoomOptional.isEmpty()) {
            throw new RuntimeException();
        } else {
            DMChatRoom dmChatRoom = dmChatRoomOptional.get();
            ChatRoomDTO chatRoomDTO = ChatRoomDTO.builder()
                    .roomId(dmChatRoom.getDmChatRoomId())
                    .build();
            chatRoomsDTO.put(dmChatRoomId, chatRoomDTO);
            return chatRoomDTO;
        }


    }
    //컨테이너 채팅 불러오기
    public Page<FindAllChatMessageByRoomIdDTO> getAllMessagePage(Long roomId, Pageable pageable){
        return chatMessageRepository.findAllMessageById(roomId, pageable);
    }

    //디엠 채팅 불러오기
    public Page<FindAllDmMessageByRoomIdDTO> getAllDMMessagePage(Long roomId, Pageable pageable){
        return dmChatMessageRepository.findAllDMMessageById(roomId,pageable);
    }

    //dm 채팅방 생성
    @Transactional
    public DMChatRoom createDMRoom(UUID sendId, UUID reciveId) {
        DMChatRoom dmChatRoom = new DMChatRoom(sendId, reciveId);
        dmChatRoomRepository.save(dmChatRoom);
        return dmChatRoom;
    }
    //dm 메세지 저장
    @Transactional
    public DMChatMessage saveDMMessage(ChatRoomMessageDTO chatMessageDTO, ChatRoomDTO roomDTO) throws JsonProcessingException {
        Optional<DMChatRoom> optionalDMChatRoom = dmChatRoomRepository.findById(roomDTO.getRoomId());
        if(optionalDMChatRoom.isEmpty()){
            throw new RuntimeException();
        }
        DMChatRoom dmChatRoom = optionalDMChatRoom.get();
        DMChatMessage dmChatMessage = new DMChatMessage(
                dmChatRoom,
                chatMessageDTO.getSender(),
                chatMessageDTO.getMsg());
        dmChatMessageRepository.save(dmChatMessage);
        return dmChatMessage;
    }

    //컨테이너 메세지 저장
    @Transactional
    public ChatRoomMessage saveContainerMessage(ChatRoomMessageDTO chatMessageDTO, ChatRoomDTO roomDTO) throws JsonProcessingException {
        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findById(roomDTO.getRoomId());
        if(optionalChatRoom.isEmpty()){
            throw new RuntimeException();
        }
        ChatRoom chatRoom = optionalChatRoom.get();
        ChatRoomMessage chatMessage = new ChatRoomMessage(
                chatRoom,
                MessageType.TALK,
                chatMessageDTO.getSender(),
                chatMessageDTO.getMsg()
                );
        chatMessageRepository.save(chatMessage);
        return chatMessage;
    }

    public String convertJSON(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        return objectMapper.writeValueAsString(object);
    }
}