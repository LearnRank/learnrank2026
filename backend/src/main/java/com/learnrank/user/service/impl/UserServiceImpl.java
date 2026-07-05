package com.learnrank.user.service.impl;

import com.learnrank.user.dto.*;
import com.learnrank.user.entity.UserEntity;
import com.learnrank.user.entity.UserRole;
import com.learnrank.user.entity.UserStatus;
import com.learnrank.user.repository.UserRepository;
import com.learnrank.user.service.UserService;
import com.learnrank.audit.service.AuditService;
import com.learnrank.common.exception.*;
import com.learnrank.common.security.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final AuditService auditService;

	public UserProfileResponse getOwnProfile(Long userId) {
	    UserEntity user = userRepository.findById(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("User", userId));
	    return toProfileResponse(user);
	}

	private UserProfileResponse toProfileResponse(UserEntity u) {
	    return new UserProfileResponse(u.getId(), u.getFullName(), u.getEmail(),
	            u.getRole().name(), u.getExperienceLevel(), u.getLearningGoals(), u.getCreatedAt());
	}

	public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
	    UserEntity user = userRepository.findById(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("User", userId));

	    user.setFullName(request.fullName());
	    user.setLearningGoals(request.learningGoals());
	    if (request.experienceLevel() != null) {
	        user.setExperienceLevel(validateExperienceLevel(request.experienceLevel()));
	    }
	    return toProfileResponse(userRepository.save(user));
	}

	private String validateExperienceLevel(String value) {
	    Set<String> allowed = Set.of("BEGINNER", "INTERMEDIATE", "ADVANCED");
	    if (!allowed.contains(value)) {
	        throw new ValidationException("experienceLevel", "must be one of " + allowed);
	    }
	    return value;
	}

	@Transactional
	public void changePassword(Long userId, ChangePasswordRequest request) {
	    UserEntity user = userRepository.findById(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("User", userId));

	    if (user.getPasswordHash() == null
	            || !passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
	        throw new InvalidCredentialsException();
	    }
	    user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
	    userRepository.save(user);

	    refreshTokenService.revokeAllForUser(userId);   // force re-login everywhere else
	    auditService.record(userId, "PASSWORD_CHANGED", "USER", userId, null);
	}

	@Transactional
	public void deleteOwnAccount(Long userId) {
	    UserEntity user = userRepository.findById(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("User", userId));

	    user.setStatus(UserStatus.DELETED);
	    user.setEmail("deleted-" + userId + "@learnrank.invalid");   // free up the email for reuse
	    userRepository.save(user);

	    refreshTokenService.revokeAllForUser(userId);
	    auditService.record(userId, "ACCOUNT_DELETED", "USER", userId, null);
	}

	public Page<AdminUserResponse> listUsers(String query, Pageable pageable) {
	    Page<UserEntity> page = (query == null || query.isBlank())
	            ? userRepository.findAll(pageable)
	            : userRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
	                    query, query, pageable);
	    return page.map(this::toAdminResponse);
	}

	private AdminUserResponse toAdminResponse(UserEntity u) {
	    return new AdminUserResponse(u.getId(), u.getFullName(), u.getEmail(),
	            u.getRole().name(), u.getStatus().name(), u.getCreatedAt(), u.getUpdatedAt());
	}
	
	@Transactional
	@PreAuthorize("hasRole('ADMIN')")
	public AdminUserResponse changeRole(Long targetUserId, ChangeRoleRequest request, Long actingAdminId) {
	    UserEntity user = userRepository.findById(targetUserId)
	            .orElseThrow(() -> new ResourceNotFoundException("User", targetUserId));

	    if (targetUserId.equals(actingAdminId) && request.role() != UserRole.ADMIN) {
	        throw new IllegalStateActionException("Admins cannot demote themselves.");
	    }

	    UserRole previousRole = user.getRole();
	    user.setRole(request.role());
	    userRepository.save(user);

	    auditService.record(actingAdminId, "ROLE_CHANGED", "USER", targetUserId,
	            Map.of("from", previousRole.name(), "to", request.role().name()));
	    return toAdminResponse(user);
	}

	@Transactional
	@PreAuthorize("hasRole('ADMIN')")
	public void suspend(Long targetUserId, Long actingAdminId) {
	    UserEntity user = userRepository.findById(targetUserId)
	            .orElseThrow(() -> new ResourceNotFoundException("User", targetUserId));
	    user.setStatus(UserStatus.SUSPENDED);
	    userRepository.save(user);

	    refreshTokenService.revokeAllForUser(targetUserId);   // kick out active sessions immediately
	    auditService.record(actingAdminId, "USER_SUSPENDED", "USER", targetUserId, null);
	}

	@Transactional
	@PreAuthorize("hasRole('ADMIN')")
	public void activate(Long targetUserId, Long actingAdminId) {
	    UserEntity user = userRepository.findById(targetUserId)
	            .orElseThrow(() -> new ResourceNotFoundException("User", targetUserId));
	    user.setStatus(UserStatus.ACTIVE);
	    userRepository.save(user);
	    auditService.record(actingAdminId, "USER_ACTIVATED", "USER", targetUserId, null);
	}
	
	@Transactional
	@PreAuthorize("hasRole('ADMIN')")
	public void hardDelete(Long targetUserId, Long actingAdminId) {
	    UserEntity user = userRepository.findById(targetUserId)
	            .orElseThrow(() -> new ResourceNotFoundException("User", targetUserId));

	    if (user.getRole() == UserRole.ADMIN) {
	        throw new IllegalStateActionException("Cannot hard-delete an ADMIN account; demote first.");
	    }

	    auditService.record(actingAdminId, "USER_HARD_DELETED", "USER", targetUserId,
	            Map.of("email", user.getEmail()));   // capture identity before the row is gone
	    userRepository.delete(user);
	}


	
}
