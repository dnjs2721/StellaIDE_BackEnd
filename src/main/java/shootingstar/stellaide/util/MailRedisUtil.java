package shootingstar.stellaide.util;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class MailRedisUtil {
    private final RedisTemplate<String, Object> mailRedis;

    public MailRedisUtil(@Qualifier("redisTemplateTwo") RedisTemplate<String, Object> mailRedis) {
        this.mailRedis = mailRedis;
    }

    public void setData(String key, String value){
        mailRedis.opsForValue().set(key, value);
    }

    public void setDataExpire(String key, String value, long milliseconds){
        mailRedis.opsForValue().set(key, value, milliseconds, TimeUnit.MILLISECONDS);
    }

    public String getData(String key){
        return (String) mailRedis.opsForValue().get(key);
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(mailRedis.hasKey(key));
    }

    public void deleteData(String key){
        mailRedis.unlink(key);
    }

    public void deleteAll() {
        Objects.requireNonNull(mailRedis.getConnectionFactory()).getConnection().serverCommands().flushAll();
    }
}
