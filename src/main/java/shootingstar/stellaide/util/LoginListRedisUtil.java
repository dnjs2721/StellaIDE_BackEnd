package shootingstar.stellaide.util;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class LoginListRedisUtil {
    private final RedisTemplate<String, Object> loginListRedis;

    public LoginListRedisUtil(@Qualifier("redisTemplateThree") RedisTemplate<String, Object> loginListRedis) {
        this.loginListRedis = loginListRedis;
    }

    public void setData(String key, String value){
        loginListRedis.opsForValue().set(key, value);
    }

    public void setDataExpire(String key, String value, long milliseconds){
        loginListRedis.opsForValue().set(key, value, milliseconds, TimeUnit.MILLISECONDS);
    }

    public String getData(String key){
        return (String) loginListRedis.opsForValue().get(key);
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(loginListRedis.hasKey(key));
    }

    public void deleteData(String key){
        loginListRedis.unlink(key);
    }

    public void deleteAll() {
        Objects.requireNonNull(loginListRedis.getConnectionFactory()).getConnection().serverCommands().flushAll();
    }
}
