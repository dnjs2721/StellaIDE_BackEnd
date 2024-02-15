package shootingstar.stellaide.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shootingstar.stellaide.entity.chat.ChatRoom;
import shootingstar.stellaide.repository.chatRoom.dto.FindAllChatMessageByRoomIdDTO;
import shootingstar.stellaide.service.ChatService;
import shootingstar.stellaide.service.dto.ChatRoomDTO;
import shootingstar.stellaide.service.ChatService;

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
    public ChatRoom createRoom(@RequestParam String containerId){
        return chatService.createRoom(containerId);
    }

    @GetMapping("chatRoom")
    public ChatRoomDTO chatRoom(@RequestParam Long roomId){
        return chatService.findRoomById(roomId);
    }

    @GetMapping("chatRoom/{roomId}")
    public ResponseEntity<?> getAllLisgtPage(@RequestParam("roomId") Long roomId,
                                             @PageableDefault(size =10) Pageable pageable){

        Page<FindAllChatMessageByRoomIdDTO> findAllChatMessageByRoomIdDTOPage = chatService.getAllMessagePage(roomId, pageable);
        return ResponseEntity.ok().body(findAllChatMessageByRoomIdDTOPage);
    }
}
