package com.learnrank.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.learnrank.auth.dto.AuthResponse;
import com.learnrank.auth.dto.LoginRequest;
import com.learnrank.auth.dto.ResgisterRequest;
import com.learnrank.auth.dto.UserResponse;
import com.learnrank.common.exception.DuplicateEmailException;
import com.learnrank.common.exception.InvalidCredentialsException;
import com.learnrank.common.security.JwtService;
import com.learnrank.user.entity.UserEntity;
import com.learnrank.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	private final JwtService jwtService;
	
	@Transactional
	public UserResponse register(ResgisterRequest request) {
		if(userRepository.existsByEmail(request.email())) {
			throw new DuplicateEmailException(request.email());
		}
		
		UserEntity user = new UserEntity();
		user.setFullName(request.fullName());
		user.setEmail(request.email());
		user.setPasswordHash(passwordEncoder.encode(request.password()));
		UserEntity saved = userRepository.save(user);
		return new UserResponse(saved.getId(), saved.getFullName(),
				saved.getEmail(), saved.getRole().name());
	}
	
	
	public AuthResponse login(LoginRequest request) {
		UserEntity user = userRepository.findByEmail(request.email())
				.orElseThrow(InvalidCredentialsException::new);
	   
		if(user.getPasswordHash() == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
		throw new InvalidCredentialsException();	
		}
	
		String accessToken = jwtService.generateAccessToken(user);
		String refreshToken = jwtService.generateRefreshToken(user);
		return new AuthResponse(accessToken, refreshToken, 3600);
	}
}
