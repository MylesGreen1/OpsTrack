package com.opstrack.audit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {
    @Mock AuditLogRepository repository;

    @Test void shouldRecordAuditEvent() {
        AuditService service = new AuditService(repository);
        AuditLog saved = new AuditLog("tech", "PATCH", "/api/maintenance-tasks/1/status", null, "HTTP 200");
        when(repository.save(any(AuditLog.class))).thenReturn(saved);
        assertSame(saved, service.record("tech", "PATCH", "/api/maintenance-tasks/1/status", null, "HTTP 200"));
        verify(repository).save(any(AuditLog.class));
    }
}
