package shootingstar.stellaide.entity;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shootingstar.stellaide.entity.chat.ContainerChatRoom;
import shootingstar.stellaide.entity.chat.ContainerChatRoomMessage;
import shootingstar.stellaide.entity.chat.MessageType;
import shootingstar.stellaide.entity.container.Container;
import shootingstar.stellaide.entity.container.ContainerType;
import shootingstar.stellaide.entity.user.User;
import shootingstar.stellaide.repository.chatRoom.container.ContainerChatRoomMessageRepository;
import shootingstar.stellaide.repository.chatRoom.container.ContainerChatRoomRepository;
import shootingstar.stellaide.repository.container.ContainerRepository;
import shootingstar.stellaide.repository.user.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class ContainerChatRoomTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContainerRepository containerRepository;
    @Autowired
    private ContainerChatRoomRepository containerChatRoomRepository;
    @Autowired
    private ContainerChatRoomMessageRepository messageRepository;


    @Test
    @Transactional
    public void createChatRoom() throws Exception {
        //given
        User user = new User("test@test.com", "test123", "test");
        userRepository.save(user);

        Container container = new Container(ContainerType.JAVA, "testContainer", "test", user);
        containerRepository.save(container);

        ContainerChatRoom newChat = new ContainerChatRoom(container, "채팅방1");

        //when
        containerChatRoomRepository.save(newChat);
        containerChatRoomRepository.flush();

        Long chatRoomId = newChat.getChatRoomId();

        Optional<ContainerChatRoom> findChat = containerChatRoomRepository.findById(chatRoomId);
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

        ContainerChatRoom newChat = new ContainerChatRoom(container, "채팅방1");
        containerChatRoomRepository.save(newChat);
        containerChatRoomRepository.flush();

        Long chatRoomId = newChat.getChatRoomId();
        Optional<ContainerChatRoom> optionalChatRoom = containerChatRoomRepository.findById(chatRoomId);

        ContainerChatRoom findChat = optionalChatRoom.get();
        Long findChatRoomId = findChat.getChatRoomId();

        ContainerChatRoomMessage containerChatRoomMessage = new ContainerChatRoomMessage(findChat, MessageType.TALK, "testSender", "testMessage");
        ContainerChatRoomMessage containerChatRoomMessage2 = new ContainerChatRoomMessage(findChat, MessageType.TALK, "testSender2", "testMessage2");
        messageRepository.save(containerChatRoomMessage);
        messageRepository.save(containerChatRoomMessage2);
        messageRepository.flush();
        findChat.addChatMessage(containerChatRoomMessage);
        findChat.addChatMessage(containerChatRoomMessage2);

        Long messageId = containerChatRoomMessage.getMessageId();

        //when
        ContainerChatRoomMessage findMessage = messageRepository.findById(messageId).get();
        System.out.println(findMessage.toString());

        Long linkChatRoomId = findMessage.getContainerChatRoom().getChatRoomId();

        //then
        assertThat(messageId).isEqualTo(findMessage.getMessageId());
        assertThat(findChatRoomId).isEqualTo(linkChatRoomId);
        System.out.println(findChat.getMessageList());
    }
}