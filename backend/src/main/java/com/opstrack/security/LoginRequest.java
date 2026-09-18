package com.opstrack.security;

public record LoginRequest(
        String username,
        String password
) {
}