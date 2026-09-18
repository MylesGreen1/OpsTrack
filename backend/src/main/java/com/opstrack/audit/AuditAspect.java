package com.opstrack.audit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@ConditionalOnBean(AuditService.class)
public class AuditAspect extends OncePerRequestFilter {

    private final AuditService auditService;

    public AuditAspect(AuditService auditService) {
        this.auditService = auditService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        filterChain.doFilter(request, response);

        String method = request.getMethod();
        String requestUri = request.getRequestURI();

        boolean isApiRequest =
                requestUri.startsWith("/api/");

        boolean isAuditRequest =
                requestUri.startsWith("/api/audit-logs");

        boolean isReadOnlyRequest =
                "GET".equalsIgnoreCase(method);

        if (isApiRequest
                && !isAuditRequest
                && !isReadOnlyRequest) {

            String username =
                    request.getUserPrincipal() == null
                            ? "anonymous"
                            : request.getUserPrincipal().getName();

            auditService.record(
                    username,
                    method,
                    requestUri,
                    null,
                    "HTTP " + response.getStatus()
            );
        }
    }
}