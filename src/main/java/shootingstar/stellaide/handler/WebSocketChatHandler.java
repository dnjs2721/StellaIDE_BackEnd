package shootingstar.stellaide.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import shootingstar.stellaide.entity.chat.ChatRoomType;
import shootingstar.stellaide.entity.chat.MessageType;
import shootingstar.stellaide.exception.CustomException;
import shootingstar.stellaide.service.ChatService;
import shootingstar.stellaide.service.dto.ChatRoomDto;
import shootingstar.stellaide.service.dto.ChatRoomMessageDto;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static shootingstar.stellaide.exception.ErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketChatHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final ChatService chatService;
    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

//    private Map<String, ArrayList<WebSocketSession>> RoomList = new ConcurrentHashMap<String, ArrayList<WebSocketSession>>();
//    private Map<WebSocketSession, String> sessionList = new ConcurrentHashMap<WebSocketSession, String>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("session Connect : {}", session.getId());
        sessions.add(session);
    }

    //세션 끊을 때
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("session Close : {}", session.getId());
        sessions.remove(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        ChatRoomMessageDto chatRoomMessageDto = null;
        try {
            chatRoomMessageDto = objectMapper.readValue(payload, ChatRoomMessageDto.class);
        } catch (Exception e) {
            log.info("chatRoomMessageDto 변환 실패 : {}", e.getMessage());
            session.close();
            return;
        }

        ChatRoomDto chatRoomDto = null;
        try {
            if (chatRoomMessageDto.getRoomType().equals(ChatRoomType.DM)) {
                chatRoomDto = chatService.findDmChatRoomById(chatRoomMessageDto.getRoomId());
            } else if (chatRoomMessageDto.getRoomType().equals(ChatRoomType.CONTAINER)) {
                chatRoomDto = chatService.findContainerChatRoomById(chatRoomMessageDto.getRoomId());
            } else if (chatRoomMessageDto.getRoomType().equals(ChatRoomType.GLOBAL)) {
                chatRoomDto = chatService.findGlobalChatRoom();
            } else {
                throw new CustomException(INCORRECT_FORMAT_ROOM_TYPE);
            }
        } catch (Exception e) {
            log.info("chatRoomDto 변환 실패 : {}", e.getMessage());
            session.close();
            return;
        }

        // DM Chat
        try {
            if(chatRoomMessageDto.getRoomType().equals(ChatRoomType.DM)){
                if(chatRoomMessageDto.getType().equals(MessageType.TALK)){
                    chatService.saveDirectMessage(chatRoomMessageDto, chatRoomDto);
                }
            }
            // Container Chat
            else if(chatRoomMessageDto.getRoomType().equals(ChatRoomType.CONTAINER)){
                if(chatRoomMessageDto.getType().equals(MessageType.TALK)){
                    chatService.saveContainerMessage(chatRoomMessageDto,chatRoomDto);
                }
            }
            // Global Chat
        } catch (Exception e) {
            log.info("채팅 내역 저장 실패: {}", e.getMessage());
            session.close();
            return;
        }

        // message 전송
        try {
            sendToEachSocket(sessions, message);
        } catch (Exception e) {
            log.info("메시지 전송 실패 : {}", e.getMessage());
            session.close();
        }
    }

    private  void sendToEachSocket(Set<WebSocketSession> sessions, TextMessage message) {
        sessions.parallelStream().forEach( roomSession -> {
            try {
                roomSession.sendMessage(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}