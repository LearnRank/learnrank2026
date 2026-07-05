package com.learnrank.user.dto;

import java.time.Instant;

public record AdminUserResponse(Long id,
		String fullName,
		String email, 
		String role,
	    String status,
	    Instant createdAt,
	    Instant updatedAt
) {

}
