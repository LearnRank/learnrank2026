package com.learnrank.user.service;
import com.learnrank.user.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserProfileResponse getOwnProfile(Long userId);

    UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request);

    void changePassword(Long userId, ChangePasswordRequest request);

    void deleteOwnAccount(Long userId);

    Page<AdminUserResponse> listUsers(String query, Pageable pageable);

    AdminUserResponse changeRole(Long targetUserId, ChangeRoleRequest request, Long actingAdminId);

    void suspend(Long targetUserId, Long actingAdminId);

    void activate(Long targetUserId, Long actingAdminId);

    void hardDelete(Long targetUserId, Long actingAdminId);
}
