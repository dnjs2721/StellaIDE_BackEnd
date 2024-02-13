package shootingstar.stellaide.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SSHConnectionUtilTest {

    @Autowired
    private SSHConnectionUtil sshConnectionUtil;

    @Test
    public void sshTest() throws Exception {
        //given
        String s = sshConnectionUtil.listDirectory("/home/shootingstar");
        System.out.println(s);

        //when

        //then

    }
}