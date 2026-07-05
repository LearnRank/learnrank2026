package com.learnrank.user.dto;

import java.time.Instant;

public record UserProfileResponse(
	    Long id, String fullName, 
	    String email, 
	    String role,
	    String experienceLevel, 
	    String learningGoals, 
	    Instant createdAt
	) {}

