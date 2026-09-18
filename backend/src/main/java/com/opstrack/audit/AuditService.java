package com.opstrack.audit;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AuditService {
    private final AuditLogRepository repository;
    public AuditService(AuditLogRepository repository) { this.repository = repository; }
    public AuditLog record(String username, String action, String resourceType, Object resourceId, String details) {
        return repository.save(new AuditLog(username, action, resourceType,
                resourceId == null ? null : resourceId.toString(), details));
    }
    public List<AuditLog> getAll() { return repository.findAll(); }
}
