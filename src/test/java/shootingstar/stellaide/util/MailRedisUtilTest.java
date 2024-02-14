package shootingstar.stellaide.util;

import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class MailRedisUtilTest {

    @Autowired
    private MailRedisUtil mailRedisUtil;

    @PostConstruct
    public void clean() {
        mailRedisUtil.deleteAll();
    }

    @Test
    public void saveAndFindAndDelete() throws Exception {
        mailRedisUtil.setData("testKey", "testValue");
        assertThat(mailRedisUtil.getData("testKey")).isEqualTo("testValue");
        mailRedisUtil.deleteData("testKey");
        assertThat(mailRedisUtil.hasKey("testKey")).isEqualTo(false);
    }
}