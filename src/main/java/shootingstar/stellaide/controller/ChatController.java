package shootingstar.stellaide.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shootingstar.stellaide.entity.chat.ChatRoom;
import shootingstar.stellaide.entity.container.Container;
import shootingstar.stellaide.repository.chatRoom.dto.FindAllChatMessageByRoomIdDTO;
import shootingstar.stellaide.service.ChatService;
import shootingstar.stellaide.service.dto.ChatRoomDTO;
import shootingstar.stellaide.service.ChatService;

import java.util.UUID;

@RestController
@RequestMapping("api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    /**
     채팅방 목록 나열
     @RequestMapping("chat/chatList")
     public String chatList(Model model){
     List<ChatRoom> roomList = chatService.findAllRoom();
     model.addAttribute("roomList",roomList);
     return "chatList";
     }
     */

    /**
     * 채팅방 생성
     * containerId 받아오기
     */
    @PostMapping("/createRoom")
    public ResponseEntity<String> createRoom(@Valid @RequestBody Container container){
        chatService.createRoom(container);
        return ResponseEntity.ok().body("채팅방 생성");
    }

    @GetMapping("chatRoom")
    public ResponseEntity<ChatRoomDTO> chatRoom(@Valid @RequestBody ChatRoom chatRoom){
        ChatRoomDTO chatRoomDTO = chatService.findRoomById(chatRoom.getChatRoomId());
        return ResponseEntity.ok().body(chatRoomDTO);
    }
    @GetMapping("chatRoom/load")
    public ResponseEntity<?> getAllLisgtPage(@RequestParam("roomId") Long roomId,
                                             @PageableDefault(size =100) Pageable pageable){

        Page<FindAllChatMessageByRoomIdDTO> findAllChatMessageByRoomIdDTOPage = chatService.getAllMessagePage(roomId, pageable);
        return ResponseEntity.ok().body(findAllChatMessageByRoomIdDTOPage);
    }
}
