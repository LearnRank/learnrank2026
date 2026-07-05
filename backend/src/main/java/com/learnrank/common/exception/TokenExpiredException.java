package com.learnrank.common.exception;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException() { super("Refresh token is invalid, expired, or has been revoked."); }
}
