package com.Local_WareHouse.backend.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class AuthResponse {
    private UUID userId;
    private String email;
    private String token;

    public AuthResponse(UUID userId, String email, String token) {
        this.userId = userId;
        this.email = email;
        this.token = token;
    }
}
