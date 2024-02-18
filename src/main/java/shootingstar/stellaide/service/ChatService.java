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
import shootingstar.stellaide.entity.chat.ChatRoom;
import shootingstar.stellaide.entity.chat.ChatRoomMessage;
import shootingstar.stellaide.entity.chat.GlobalChatRoom;
import shootingstar.stellaide.entity.chat.MessageType;
import shootingstar.stellaide.repository.chatRoom.ChatRoomMessageRepository;
import shootingstar.stellaide.repository.chatRoom.ChatRoomRepository;
import shootingstar.stellaide.repository.chatRoom.GlobalChatRoomRepository;
import shootingstar.stellaide.repository.chatRoom.dto.FindAllChatMessageByRoomIdDTO;
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
    private Map<String, ChatRoomMessageDTO> chatRoomMessageDTO;

    @PostConstruct
    private void init() {
        chatRoomsDTO = new LinkedHashMap<>();
    }

    /*
    채팅방 목록 불러오기
     */
    public List<ChatRoom> findAllRoom() {
        return chatRoomRepository.findAll();
    }

    public ChatRoomDTO findRoomById(Long chatRoomId) {
        log.info("message : {}",chatRoomId.toString());
        if(chatRoomId == 999L){
            ChatRoomDTO chatRoomDTO = ChatRoomDTO.builder()
                    .roomId(999L)
                    .name("globalChat")
                    .build();
            chatRoomsDTO.put(chatRoomId,chatRoomDTO);
            return chatRoomDTO;
        }
        else {
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

    public Page<FindAllChatMessageByRoomIdDTO> getAllMessagePage(Long roomId, Pageable pageable){
//        log.info(chatMessageRepository.findAllMessageById(roomId, pageable).toString());
        return chatMessageRepository.findAllMessageById(roomId, pageable);
    }

    @Transactional
    public GlobalChatRoom createRoom(Long roomId) {
            GlobalChatRoom globalChatRoom = new GlobalChatRoom(999L);
            globalChatRoomRepository.save(globalChatRoom);
            return globalChatRoom;
    }

    @Transactional
    public ChatRoomMessage saveMessage(ChatRoomMessageDTO chatMessageDTO, ChatRoomDTO roomDTO) throws JsonProcessingException {
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