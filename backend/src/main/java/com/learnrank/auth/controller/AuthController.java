package com.learnrank.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learnrank.auth.dto.AuthResponse;
import com.learnrank.auth.dto.LoginRequest;
import com.learnrank.auth.dto.RefreshTokenRequest;
import com.learnrank.auth.dto.ResgisterRequest;
import com.learnrank.auth.dto.UserResponse;
import com.learnrank.auth.service.AuthService;
import com.learnrank.common.exception.ResourceNotFoundException;
import com.learnrank.common.exception.TokenExpiredException;
import com.learnrank.common.security.JwtService;
import com.learnrank.common.security.RefreshTokenService;
import com.learnrank.user.entity.UserEntity;
import com.learnrank.user.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
	
	
	private final AuthService authService;
	private final UserRepository userRepository;
	private final JwtService jwtService;
	private final RefreshTokenService refreshTokenService;
	
	@PostMapping("/register")
	public ResponseEntity<UserResponse> register(@Valid @RequestBody ResgisterRequest request){
		
		UserResponse response = authService.register(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login (@Valid @RequestBody LoginRequest request){
		return ResponseEntity.ok(authService.login(request));
	}
	
	@PostMapping("/refresh-token")
	public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
	    if (!jwtService.isTokenValid(request.refreshToken())) {
	        throw new TokenExpiredException();
	    }
	    Long userId = jwtService.extractUserId(request.refreshToken());
	    if (!refreshTokenService.isValid(userId, request.refreshToken())) {
	        throw new TokenExpiredException();   // revoked (logout / password change / suspension)
	    }

	    UserEntity user = userRepository.findById(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("User", userId));
	    String newAccessToken = jwtService.generateAccessToken(user);
	    return ResponseEntity.ok(new AuthResponse(newAccessToken, request.refreshToken(), 3600, userId, user.getFullName(), user.getRole().name()));
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request,
	                                    @AuthenticationPrincipal Long userId) {
	    refreshTokenService.revoke(userId, request.refreshToken());
	    return ResponseEntity.noContent().build();
	}


}
