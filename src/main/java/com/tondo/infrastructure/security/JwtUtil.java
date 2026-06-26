package com.tondo.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_TYPE = "type";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.refresh-expiration:2592000000}")
    private long refreshExpiration;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateAccessToken(Long userId, String role) {
        return buildToken(userId, role, TYPE_ACCESS, expiration);
    }

    public String generateRefreshToken(Long userId, String role) {
        return buildToken(userId, role, TYPE_REFRESH, refreshExpiration);
    }

    private String buildToken(Long userId, String role, String type, long ttlMillis) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim(CLAIM_ROLE, role)
                .claim(CLAIM_TYPE, type)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ttlMillis))
                .signWith(getKey())
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        return Long.parseLong(getClaims(token).getSubject());
    }

    public String getRoleFromToken(String token) {
        return getClaims(token).get(CLAIM_ROLE, String.class);
    }

    public boolean isAccessToken(String token) {
        String type = getClaims(token).get(CLAIM_TYPE, String.class);
        return type == null || TYPE_ACCESS.equals(type);
    }

    public boolean isRefreshToken(String token) {
        return TYPE_REFRESH.equals(getClaims(token).get(CLAIM_TYPE, String.class));
    }

    public long getRemainingMillis(String token) {
        try {
            Date expiration = getClaims(token).getExpiration();
            return Math.max(expiration.getTime() - System.currentTimeMillis(), 0);
        } catch (ExpiredJwtException ex) {
            return 0;
        }
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getAccessExpiration() {
        return expiration;
    }
}
