package com.learnrank.common.security;

public interface RefreshTokenService {

    void store(Long userId, String refreshToken);

    boolean isValid(Long userId, String refreshToken);

    void revoke(Long userId, String refreshToken);

    void revokeAllForUser(Long userId);
}

