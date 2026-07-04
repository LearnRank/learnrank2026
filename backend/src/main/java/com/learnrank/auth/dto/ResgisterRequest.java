package com.learnrank.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResgisterRequest(
		
		@NotBlank
		@Size(min = 2, max = 150)
		String fullName,
		
		@NotBlank
		@Email
		String email,
		
		@NotBlank
		@Size(min = 8, max = 100)
		@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
        message = "password must contain at least one letter and one number")
		String password
		) {

}
