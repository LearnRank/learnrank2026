package com.learnrank.audit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learnrank.audit.entity.AuditLogEntity;

public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {
}
