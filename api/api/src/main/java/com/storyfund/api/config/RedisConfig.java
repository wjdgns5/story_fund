package com.storyfund.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // key, value 모두 String 으로 직렬화
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());

        return template;
    }
}

/**
 * 직렬화가 뭔지?
 *
 * Java 객체를 Redis 에 저장할 때 바이트로 변환해야 해요
 * 기본 설정은 이상한 문자로 저장되는데
 * StringRedisSerializer 를 쓰면 사람이 읽을 수 있는 문자열로 저장돼요.
 *--------------------------------------------------------------------
 * 기본 직렬화:  \xac\xed\x00\x05t\x00...  (알 수 없는 문자)
 * String 직렬화: 483920                    (그냥 문자열)
 */
