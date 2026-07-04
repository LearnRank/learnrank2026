package com.learnrank.common.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.learnrank.user.entity.UserEntity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	
	@Value("${app.jwt.secret}")
	private String secret;
	
	@Value("${app.jwt.access-token-ttl-ms}")
	private long accessTokenTtlMs;
	
	@Value("${app.jwt.refresh-token-ttl-ms}")
	private long refreshTokenTtlMs;
	
	private SecretKey key() {
		return Keys.hmacShaKeyFor(secret.getBytes());
		
	}
	
	public String generateAccessToken(UserEntity user) {
		return buildToken(user, accessTokenTtlMs);
	}
	
	public String generateRefreshToken(UserEntity user) {
		return buildToken(user, refreshTokenTtlMs);
	}
	
	public String extractRole(String token) {
        return parseClaims(token).get("role", String.class);
    }


	
	public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

	
	public String buildToken(UserEntity user, long ttlMs) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + ttlMs);
		return Jwts.builder().subject(String.valueOf(user.getId()))
				.claim("email",  user.getEmail())
				.claim("role", user.getRole().name())
				.issuedAt(now)
				.expiration(expiry)
				.signWith(key())
				.compact();
	}
	
	public Long extractUserId(String token) {
		return Long.valueOf(parseClaims(token).getSubject());
	}
	
	private Claims parseClaims(String token) {
		return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload();
	}
	
	

}
