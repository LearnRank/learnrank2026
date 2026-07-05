package com.learnrank.admin.controller;

import com.learnrank.user.dto.*;
import com.learnrank.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<AdminUserResponse>> listUsers(
            @RequestParam(required = false) String q, Pageable pageable) {
        return ResponseEntity.ok(userService.listUsers(q, pageable));
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<AdminUserResponse> changeRole(
            @PathVariable Long id, @Valid @RequestBody ChangeRoleRequest request,
            @AuthenticationPrincipal Long adminId) {
        return ResponseEntity.ok(userService.changeRole(id, request, adminId));
    }

    @PatchMapping("/{id}/suspend")
    public ResponseEntity<Void> suspend(@PathVariable Long id, @AuthenticationPrincipal Long adminId) {
        userService.suspend(id, adminId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id, @AuthenticationPrincipal Long adminId) {
        userService.activate(id, adminId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> hardDelete(@PathVariable Long id, @AuthenticationPrincipal Long adminId) {
        userService.hardDelete(id, adminId);
        return ResponseEntity.noContent().build();
    }
}
