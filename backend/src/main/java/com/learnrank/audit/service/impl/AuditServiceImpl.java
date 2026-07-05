package com.learnrank.audit.service.impl;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.learnrank.audit.entity.AuditLogEntity;
import com.learnrank.audit.repository.AuditLogRepository;
import com.learnrank.audit.service.AuditService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public void record(Long actorUserId, String eventType, String entityType,
                        Long entityId, Map<String, Object> metadata) {
        AuditLogEntity entry = new AuditLogEntity();
        entry.setUserId(actorUserId);
        entry.setEventType(eventType);
        entry.setEntityType(entityType);
        entry.setEntityId(entityId);
        entry.setMetadata(metadata);
        auditLogRepository.save(entry);
    }
}
