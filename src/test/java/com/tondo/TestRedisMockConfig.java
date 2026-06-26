package com.tondo;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration
public class TestRedisMockConfig {

    @Bean
    @Primary
    public StringRedisTemplate stringRedisTemplate() {
        StringRedisTemplate template = mock(StringRedisTemplate.class);
        ValueOperations<String, String> ops = mock(ValueOperations.class);
        when(template.opsForValue()).thenReturn(ops);
        when(ops.increment(org.mockito.ArgumentMatchers.anyString())).thenReturn(1L);
        return template;
    }

    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {
        return mock(RedisConnectionFactory.class);
    }
}
