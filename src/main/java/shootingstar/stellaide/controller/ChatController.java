package shootingstar.stellaide.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shootingstar.stellaide.entity.chat.ChatRoom;
import shootingstar.stellaide.entity.chat.DMChatRoom;
import shootingstar.stellaide.repository.chatRoom.dto.FindAllChatMessageByRoomIdDTO;
import shootingstar.stellaide.repository.chatRoom.dto.FindAllDmMessageByRoomIdDTO;
import shootingstar.stellaide.service.ChatService;
import shootingstar.stellaide.service.dto.ChatRoomDTO;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final ChatService chatService;


    /**
     * 채팅방 목록 나열
     */
     @RequestMapping("chat/chatList")
     public ResponseEntity<List<DMChatRoom>> chatList(){
     List<DMChatRoom> roomList = chatService.findAllRoom();
     return ResponseEntity.ok().body(roomList);
     }


    /**
     * DM 채팅방 생성
     */
    @PostMapping("/createDMRoom")
    public ResponseEntity<String> createRoom(@RequestParam("sendId") UUID sendId, @RequestParam("reciveId") UUID receivdId){
        chatService.createDMRoom(sendId, receivdId);
        return ResponseEntity.ok().body("채팅방 생성");
    }

    @GetMapping("/dmChatRoom")
    public ResponseEntity<ChatRoomDTO> dmChatRoom(@Valid @RequestBody DMChatRoom dmChatRoom){
        ChatRoomDTO chatRoomDTO = chatService.findDMRoomById(dmChatRoom.getDmChatRoomId());
        return ResponseEntity.ok().body(chatRoomDTO);
    }

    @GetMapping("/dmChatRoom/loadHistory")
    public ResponseEntity<?> getAllDMListPage(@RequestParam("chatRoomId") Long chatRoomId,
                                                @PageableDefault(size =100) Pageable pageable){
        Page<FindAllDmMessageByRoomIdDTO> findAllDmMessageByRoomIdDTOPage = chatService.getAllDMMessagePage(chatRoomId,pageable);
        return ResponseEntity.ok().body(findAllDmMessageByRoomIdDTOPage);
    }

    /**
     * 글로벌 채팅방
     * @param chatRoomId
     */
    @GetMapping("/globalChatRoom")
    public ResponseEntity<ChatRoomDTO> globalChatRoom(@RequestParam(value = "chatRoomId") Long chatRoomId){
        ChatRoomDTO chatRoomDTO = chatService.findRoomById(chatRoomId);
        return ResponseEntity.ok().body(chatRoomDTO);
    }

    /**
     * 컨테이너 채팅 내역 불러오기
     * @param roomId
     * @param pageable
     */
    @GetMapping("/container/loadHistory")
    public ResponseEntity<?> getAllListPage(@RequestParam("roomId") Long roomId,
                                             @PageableDefault(size =100) Pageable pageable){

        Page<FindAllChatMessageByRoomIdDTO> findAllChatMessageByRoomIdDTOPage = chatService.getAllMessagePage(roomId, pageable);
        return ResponseEntity.ok().body(findAllChatMessageByRoomIdDTOPage);
    }
}
