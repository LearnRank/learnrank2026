package com.learnrank.audit.service;

import java.util.Map;

public interface AuditService {

    void record(Long actorUserId, String eventType, String entityType,
                Long entityId, Map<String, Object> metadata);
}
