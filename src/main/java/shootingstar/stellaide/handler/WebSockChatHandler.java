package shootingstar.stellaide.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.w3c.dom.Text;
import shootingstar.stellaide.entity.chat.ChatRoom;
import shootingstar.stellaide.entity.chat.ChatRoomMessage;
import shootingstar.stellaide.repository.chatRoom.ChatRoomMessageRepository;
import shootingstar.stellaide.service.ChatService;
import shootingstar.stellaide.service.dto.ChatRoomDTO;
import shootingstar.stellaide.service.dto.ChatRoomMessageDTO;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSockChatHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final ChatService chatService;
    private final ChatRoomMessageRepository chatRoomMessageRepository;
    private Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<WebSocketSession>());

//    private Map<String, ArrayList<WebSocketSession>> RoomList = new ConcurrentHashMap<String, ArrayList<WebSocketSession>>();
//    private Map<WebSocketSession, String> sessionList = new ConcurrentHashMap<WebSocketSession, String>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info(session.getId());
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        ChatRoomMessageDTO chatMessageDTO = objectMapper.readValue(payload, ChatRoomMessageDTO.class);
        ChatRoomDTO room = chatService.findRoomById(chatMessageDTO.getRoomId());

        //Set<WebSocketSession>
        sessions = room.getSessions();

        if (chatMessageDTO.getType().equals(ChatRoomMessageDTO.MessageType.ENTER)) {
            sessions.add(session);
            chatMessageDTO.setMsg(chatMessageDTO.getSender() + "님이 입장했습니다.");
            sendToEachSocket(sessions, new TextMessage(objectMapper.writeValueAsString(chatMessageDTO)));
            chatService.saveContainerMessage(chatMessageDTO,room);
        }
        else if (chatMessageDTO.getRoomType().equals(ChatRoomMessageDTO.RoomType.GLOBAL)) {
            sendToEachSocket(sessions, new TextMessage(objectMapper.writeValueAsString(chatMessageDTO)));
        }
        else if(chatMessageDTO.getRoomType().equals(ChatRoomMessageDTO.RoomType.DM)){
            chatService.saveDMMessage(chatMessageDTO, room);
            sendToEachSocket(sessions, new TextMessage(objectMapper.writeValueAsString(chatMessageDTO)));
        }
        else{
            sendToEachSocket(sessions, new TextMessage(objectMapper.writeValueAsString(chatMessageDTO)));
            chatService.saveContainerMessage(chatMessageDTO,room);
        }
    }


    private  void sendToEachSocket(Set<WebSocketSession> sessions, TextMessage message){
        sessions.parallelStream().forEach( roomSession -> {
            try {
                roomSession.sendMessage(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }



    //세션 끊을 때
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }

    public String convertJSON(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        return objectMapper.writeValueAsString(object);
    }

}