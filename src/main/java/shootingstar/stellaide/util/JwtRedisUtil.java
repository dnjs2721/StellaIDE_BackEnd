package shootingstar.stellaide.util;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class JwtRedisUtil {
    private final RedisTemplate<String, Object> jwtRedis;

    public JwtRedisUtil(@Qualifier("redisTemplate") RedisTemplate<String, Object> jwtRedis) {
        this.jwtRedis = jwtRedis;
    }

    public void setData(String key, String value){
        jwtRedis.opsForValue().set(key, value);
    }

    public void setDataExpire(String key, String value, long milliseconds){
        jwtRedis.opsForValue().set(key, value, milliseconds, TimeUnit.MILLISECONDS);
    }

    public String getData(String key){
        return (String) jwtRedis.opsForValue().get(key);
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(jwtRedis.hasKey(key));
    }

    public void deleteData(String key){
        jwtRedis.unlink(key);
    }

    public void deleteAll() {
        Objects.requireNonNull(jwtRedis.getConnectionFactory()).getConnection().serverCommands().flushAll();
    }
}
