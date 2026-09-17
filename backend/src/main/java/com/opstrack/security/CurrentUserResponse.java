package com.opstrack.security;

public record CurrentUserResponse(
        String username,
        Role role,
        boolean enabled
) {
}