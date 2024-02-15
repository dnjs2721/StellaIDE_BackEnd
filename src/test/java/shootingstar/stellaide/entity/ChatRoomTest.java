package shootingstar.stellaide.entity;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shootingstar.stellaide.entity.chat.ChatRoom;
import shootingstar.stellaide.entity.chat.ChatRoomMessage;
import shootingstar.stellaide.entity.chat.MessageType;
import shootingstar.stellaide.entity.container.Container;
import shootingstar.stellaide.entity.container.ContainerType;
import shootingstar.stellaide.entity.user.User;
import shootingstar.stellaide.repository.chatRoom.ChatRoomMessageRepository;
import shootingstar.stellaide.repository.chatRoom.ChatRoomRepository;
import shootingstar.stellaide.repository.container.ContainerRepository;
import shootingstar.stellaide.repository.user.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class ChatRoomTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContainerRepository containerRepository;
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private ChatRoomMessageRepository messageRepository;


    @Test
    @Transactional
    public void createChatRoom() throws Exception {
        //given
        User user = new User("test@test.com", "test123", "test");
        userRepository.save(user);

        Container container = new Container(ContainerType.JAVA, "testContainer", "test", user);
        containerRepository.save(container);

        ChatRoom newChat = new ChatRoom(container, "채팅방1");

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
        User user = new User("test@test.com", "test123", "test");
        userRepository.save(user);

        Container container = new Container(ContainerType.JAVA, "testContainer", "test", user);
        containerRepository.save(container);

        ChatRoom newChat = new ChatRoom(container, "채팅방1");
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