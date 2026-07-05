package com.learnrank.common.security.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.learnrank.common.security.RefreshTokenService;

import java.security.MessageDigest;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final StringRedisTemplate redis;
    private static final Duration TTL = Duration.ofDays(14);

    @Override
    public void store(Long userId, String refreshToken) {
        redis.opsForValue().set(key(userId, refreshToken), "1", TTL);
    }

    @Override
    public boolean isValid(Long userId, String refreshToken) {
        return redis.hasKey(key(userId, refreshToken));
    }

    @Override
    public void revoke(Long userId, String refreshToken) {
        redis.delete(key(userId, refreshToken));
    }

    @Override
    public void revokeAllForUser(Long userId) {
        var keys = redis.keys("refresh:" + userId + ":*");
        if (keys != null && !keys.isEmpty()) redis.delete(keys);
    }

    private String key(Long userId, String token) {
        return "refresh:" + userId + ":" + sha256(token);
    }

    private String sha256(String value) {
        try {
            var digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}
