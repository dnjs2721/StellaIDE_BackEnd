package shootingstar.stellaide.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String redisHost;
    @Value("${spring.data.redis.port}")
    private int redisPort1; // jwt 레디스 포트
    @Value("${spring.data.redis.port2}")
    private int redisPort2; // mail 레디스 포트
    @Value("${spring.data.redis.port3}")
    private int redisPort3; // loginList 레디스 포트
    @Value("${spring.data.redis.password}")
    private String redisPassword;


    // JWT 레디스 빈 등록
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(redisHost);
        redisConfiguration.setPort(redisPort1);
        redisConfiguration.setPassword(redisPassword);
        return new LettuceConnectionFactory(redisConfiguration);
    }

    // mail 레디스 빈 등록
    @Bean
    public RedisConnectionFactory redisConnectionFactoryTwo() {
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(redisHost);
        redisConfiguration.setPort(redisPort2);
        redisConfiguration.setPassword(redisPassword);
        return new LettuceConnectionFactory(redisConfiguration);
    }

    // login 레디스 빈 등록
    @Bean
    public RedisConnectionFactory redisConnectionFactoryThree() {
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(redisHost);
        redisConfiguration.setPort(redisPort3);
        redisConfiguration.setPassword(redisPassword);
        return new LettuceConnectionFactory(redisConfiguration);
    }

    // JWT 레디스 템플릿 등록
    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());   //connection
        redisTemplate.setKeySerializer(new StringRedisSerializer());    // key
        redisTemplate.setValueSerializer(new StringRedisSerializer());  // value
        return redisTemplate;
    }

    // mail 레디스 템플릿 등록
    @Bean
    public RedisTemplate<?, ?> redisTemplateTwo() {
        RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactoryTwo());   //connection
        redisTemplate.setKeySerializer(new StringRedisSerializer());    // key
        redisTemplate.setValueSerializer(new StringRedisSerializer());  // value
        return redisTemplate;
    }

    // loginList 레디스 템플릿 등록
    @Bean
    public RedisTemplate<?, ?> redisTemplateThree() {
        RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactoryThree());   //connection
        redisTemplate.setKeySerializer(new StringRedisSerializer());    // key
        redisTemplate.setValueSerializer(new StringRedisSerializer());  // value
        return redisTemplate;
    }

    // 레디스에 저장되는 데이터 JSON 타입으로 직렬화
    @Bean
    public RedisSerializer<Object> defaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }
}
