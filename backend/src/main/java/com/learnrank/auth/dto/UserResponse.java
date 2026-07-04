package com.learnrank.auth.dto;

public record UserResponse(
		Long id,
		String fullname,
		String email,
		String role
		) {

}
