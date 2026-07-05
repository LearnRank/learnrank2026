package com.learnrank.auth.dto;

public record AuthResponse(
	    String accessToken,
	    String refreshToken,
	    long expiresIn,
	    Long userId,
	    String fullName,
	    String role
	) {}
