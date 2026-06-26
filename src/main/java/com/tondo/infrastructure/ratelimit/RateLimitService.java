package com.tondo.infrastructure.ratelimit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final StringRedisTemplate redisTemplate;

    /**
     * 固定窗口计数限流。
     */
    public boolean tryAcquire(String key, int limit, int windowSeconds) {
        try {
            Long count = redisTemplate.opsForValue().increment(key);
            if (count != null && count == 1L) {
                redisTemplate.expire(key, Duration.ofSeconds(windowSeconds));
            }
            return count != null && count <= limit;
        } catch (Exception ex) {
            return true;
        }
    }
}
