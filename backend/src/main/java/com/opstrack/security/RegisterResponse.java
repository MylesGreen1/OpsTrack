package com.opstrack.security;

public class RegisterResponse {

    private final String username;
    private final Role role;
    private final boolean enabled;

    public RegisterResponse(
            String username,
            Role role,
            boolean enabled
    ) {
        this.username = username;
        this.role = role;
        this.enabled = enabled;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    public boolean isEnabled() {
        return enabled;
    }
}