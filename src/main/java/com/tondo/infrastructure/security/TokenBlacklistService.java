package com.tondo.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private static final String PREFIX = "token:blacklist:";

    private final StringRedisTemplate redisTemplate;

    public void blacklist(String token, long ttlMillis) {
        if (token == null || token.isBlank() || ttlMillis <= 0) {
            return;
        }
        redisTemplate.opsForValue().set(key(token), "1", Duration.ofMillis(ttlMillis));
    }

    public boolean isBlacklisted(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key(token)));
        } catch (Exception ex) {
            return false;
        }
    }

    private String key(String token) {
        return PREFIX + sha256(token);
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            return Integer.toHexString(value.hashCode());
        }
    }
}
