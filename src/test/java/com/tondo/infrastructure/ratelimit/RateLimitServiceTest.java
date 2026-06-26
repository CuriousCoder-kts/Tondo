package com.tondo.infrastructure.ratelimit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RateLimitServiceTest {

    private StringRedisTemplate redisTemplate;
    private ValueOperations<String, String> valueOps;
    private RateLimitService rateLimitService;

    @BeforeEach
    void setUp() {
        redisTemplate = mock(StringRedisTemplate.class);
        valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        rateLimitService = new RateLimitService(redisTemplate);
    }

    @Test
    void tryAcquire_allowsWithinLimit() {
        when(valueOps.increment("rate:login:127.0.0.1")).thenReturn(1L, 2L);
        assertTrue(rateLimitService.tryAcquire("rate:login:127.0.0.1", 5, 60));
        assertTrue(rateLimitService.tryAcquire("rate:login:127.0.0.1", 5, 60));
    }

    @Test
    void tryAcquire_blocksWhenExceeded() {
        when(valueOps.increment("rate:login:127.0.0.1")).thenReturn(6L);
        assertFalse(rateLimitService.tryAcquire("rate:login:127.0.0.1", 5, 60));
    }

    @Test
    void tryAcquire_failOpenWhenRedisUnavailable() {
        when(redisTemplate.opsForValue()).thenThrow(new RuntimeException("redis down"));
        assertTrue(rateLimitService.tryAcquire("rate:login:127.0.0.1", 5, 60));
    }
}
