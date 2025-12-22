package com.Local_WareHouse.backend.controller;

import com.Local_WareHouse.backend.service.AuthService;
import com.Local_WareHouse.backend.dto.LoginRequest;
import com.Local_WareHouse.backend.dto.RegisterRequest;
import com.Local_WareHouse.backend.dto.AuthResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// import com.Local_WareHouse.backend.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")

public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request) {

        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }
}
