package shootingstar.stellaide.entity;

import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import shootingstar.stellaide.repository.chatRoom.ChatRoomMessageRepository;
import shootingstar.stellaide.repository.chatRoom.ChatRoomRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChatRoomTest {

    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private ChatRoomMessageRepository messageRepository;


    @Test
    @Transactional
    public void createChatRoom() throws Exception {
        //given
        ChatRoom newChat = new ChatRoom(1L, "채팅방1");

        //when
        chatRoomRepository.save(newChat);
        chatRoomRepository.flush();

        Long chatRoomId = newChat.getChatRoomId();

        Optional<ChatRoom> findChat = chatRoomRepository.findById(chatRoomId);
        Long findChatId = findChat.get().getChatRoomId();

        //then
        assertThat(chatRoomId).isEqualTo(findChatId);
    }

    @Test
    @Transactional
    public void createChatRoomMessage() throws Exception {
        //given
        ChatRoom newChat = new ChatRoom(1L, "채팅방1");
        chatRoomRepository.save(newChat);
        chatRoomRepository.flush();

        Long chatRoomId = newChat.getChatRoomId();
        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findById(chatRoomId);
        ChatRoom findChat = optionalChatRoom.get();
        Long findChatRoomId = findChat.getChatRoomId();

        ChatRoomMessage chatRoomMessage = new ChatRoomMessage(findChat, MessageType.TALK, "testSender", "testMessage");
        ChatRoomMessage chatRoomMessage2 = new ChatRoomMessage(findChat, MessageType.TALK, "testSender2", "testMessage2");
        messageRepository.save(chatRoomMessage);
        messageRepository.save(chatRoomMessage2);
        messageRepository.flush();
        findChat.addChatMessage(chatRoomMessage);
        findChat.addChatMessage(chatRoomMessage2);

        Long messageId = chatRoomMessage.getMessageId();

        //when
        ChatRoomMessage findMessage = messageRepository.findById(messageId).get();
        System.out.println(findMessage.toString());

        Long linkChatRoomId = findMessage.getChatRoom().getChatRoomId();

        //then
        assertThat(messageId).isEqualTo(findMessage.getMessageId());
        assertThat(findChatRoomId).isEqualTo(linkChatRoomId);
        System.out.println(findChat.getMessageList());
    }
}