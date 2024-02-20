package shootingstar.stellaide.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shootingstar.stellaide.entity.chat.DirectChatRoom;
import shootingstar.stellaide.repository.chatRoom.dto.FindAllChatMessageByRoomIdDto;
import shootingstar.stellaide.repository.chatRoom.dto.FindAllDmMessageByRoomIdDto;
import shootingstar.stellaide.service.ChatService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final ChatService chatService;

    /**
     * 컨테이너 채팅 내역 불러오기
     */
    @GetMapping("/container/loadHistory")
    public ResponseEntity<?> getAllListPage(@NotNull @RequestParam("roomId") Long roomId,
                                             @PageableDefault(size =100) Pageable pageable){
        Page<FindAllChatMessageByRoomIdDto> findAllChatMessageByRoomIdDTOPage = chatService.getAllMessagePage(roomId, pageable);
        return ResponseEntity.ok().body(findAllChatMessageByRoomIdDTOPage);
    }


    /**
     * 채팅방 목록 나열
     */
    @GetMapping("/chatList")
    public ResponseEntity<DirectChatRoom> chatList(){
        log.info("api :{}",chatList());
        List<DirectChatRoom> roomList = chatService.findAllRoom();
        return ResponseEntity.ok((DirectChatRoom) roomList);
    }

    @GetMapping("/dmChatRoom/loadHistory")
    public ResponseEntity<?> getAllDMListPage(@RequestParam("chatRoomId") Long chatRoomId,
                                              @PageableDefault(size =100) Pageable pageable){
        Page<FindAllDmMessageByRoomIdDto> findAllDmMessageByRoomIdDTOPage = chatService.getAllDMMessagePage(chatRoomId,pageable);
        return ResponseEntity.ok().body(findAllDmMessageByRoomIdDTOPage);
    }

    /**
     * DM 채팅방 생성
     */
    @PostMapping("/createDMRoom")
    public ResponseEntity<String> createRoom(@RequestParam("sendId") String sendId, @RequestParam("receivdId") String receivdId){
        chatService.createDirectChatRoom(sendId, receivdId);
        return ResponseEntity.ok().body("채팅방 생성");
    }

//    @GetMapping("/dmChatRoom")
//    public ResponseEntity<ChatRoomDto> dmChatRoom(@RequestParam("chatRoomId") Long dmChatRoomId){
//        ChatRoomDto chatRoomDTO = chatService.findDMRoomById(dmChatRoomId);
//        return ResponseEntity.ok().body(chatRoomDTO);
//    }
}
