package shootingstar.stellaide.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisUtil {
    private final RedisTemplate<String, Object> redisTemplate;

    public void setData(String key, String value){
        redisTemplate.opsForValue().set(key, value);
    }

    public void setDataExpire(String key, String value,int minutes){
        redisTemplate.opsForValue().set(key, value, minutes, TimeUnit.MINUTES);
    }

    public String getData(String key){
        return (String) redisTemplate.opsForValue().get(key);
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void deleteData(String key){
        redisTemplate.unlink(key);
    }

    public void deleteAll() {
        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().serverCommands().flushAll();
    }
}
