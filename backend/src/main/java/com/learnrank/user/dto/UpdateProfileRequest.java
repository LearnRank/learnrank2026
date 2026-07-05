package com.learnrank.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(	@NotBlank
		@Size(min =2, max = 150)
		String fullName,
		
		@Size(max=2000)
		String learningGoals,
		
		String experienceLevel) {

}
