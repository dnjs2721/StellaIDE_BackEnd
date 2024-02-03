package shootingstar.stellaide.util;

import jakarta.annotation.PostConstruct;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class RedisUtilTest {

    @Autowired
    private RedisUtil redisUtil;

    @PostConstruct
    public void clean() {
        redisUtil.deleteAll();
    }

    @Test
    public void saveAndFindAndDelete() throws Exception {
        redisUtil.setData("testKey", "testValue");
        assertThat(redisUtil.getData("testKey")).isEqualTo("testValue");
        redisUtil.deleteData("testKey");
        assertThat(redisUtil.hasKey("testKey")).isEqualTo(false);
    }
}