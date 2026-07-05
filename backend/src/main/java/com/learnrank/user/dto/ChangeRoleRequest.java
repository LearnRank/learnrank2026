package com.learnrank.user.dto;

import com.learnrank.user.entity.UserRole;

import jakarta.validation.constraints.NotNull;

public record ChangeRoleRequest(
		@NotNull UserRole role) {

}
