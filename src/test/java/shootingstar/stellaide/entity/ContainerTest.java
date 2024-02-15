package shootingstar.stellaide.entity;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import shootingstar.stellaide.entity.container.Container;
import shootingstar.stellaide.entity.container.ContainerType;
import shootingstar.stellaide.entity.user.User;
import shootingstar.stellaide.repository.container.ContainerRepository;
import shootingstar.stellaide.repository.user.UserRepository;
import shootingstar.stellaide.repository.sharedUserContainer.SharedUserContainerRepository;

@SpringBootTest
class ContainerTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SharedUserContainerRepository sharedUserContainerRepository;
    @Autowired
    private ContainerRepository containerRepository;

    @Test
    @Transactional
    public void createContainer() throws Exception {
        //given
        User user = new User("test@test.com", "test123", "test");
        User user2 = new User("test2@test.com", "test123", "test");

        userRepository.save(user);
        userRepository.save(user2);
        userRepository.flush();

        User findUser1 = userRepository.findById(user.getUserId()).get();
        User findUser2 = userRepository.findById(user2.getUserId()).get();

        Container container = new Container(ContainerType.JAVA, "testContainer", "test", findUser1);
        containerRepository.save(container);
        containerRepository.flush();

        Container findContainer = containerRepository.findById(container.getContainerId()).get();

        SharedUserContainer sharedUserContainer = new SharedUserContainer(findContainer, findUser2);
        sharedUserContainerRepository.save(sharedUserContainer);
        sharedUserContainerRepository.flush();

        //when
        containerRepository.delete(findContainer);
        //then

    }

}