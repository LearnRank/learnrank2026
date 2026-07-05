package com.learnrank.user.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.learnrank.user.entity.UserEntity;
import com.learnrank.user.entity.UserRole;
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

	Optional<UserEntity> findByEmail(String email);
	
	boolean existsByEmail(String email);
	
	Page<UserEntity> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email, Pageable pageable);
	
	Page<UserEntity> findByRole(UserRole role, Pageable pageable);
}
