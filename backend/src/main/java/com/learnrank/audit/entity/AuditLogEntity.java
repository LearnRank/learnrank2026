package com.learnrank.audit.entity;
import jakarta.persistence.*;
import lombok.Getter; import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.Map;

@Entity
@Table(name = "audit_log")
@Getter @Setter @NoArgsConstructor
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;              // nullable — null for system-initiated events

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;         // e.g. LOGIN, ROLE_CHANGED, PASSWORD_CHANGED

    @Column(name = "entity_type", length = 50)
    private String entityType;        // e.g. USER, RESOURCE, REVIEW

    @Column(name = "entity_id")
    private Long entityId;

    @Convert(converter = JsonMapConverter.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}

