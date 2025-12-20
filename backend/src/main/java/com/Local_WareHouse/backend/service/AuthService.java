package com.Local_WareHouse.backend.service;

import com.Local_WareHouse.backend.dto.*;
import com.Local_WareHouse.backend.entity.User;
import com.Local_WareHouse.backend.repository.UserRepository;
import com.Local_WareHouse.backend.security.JwtUtil;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // ---------------- REGISTER ----------------
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(
            passwordEncoder.encode(request.getPassword())
        );

        userRepository.save(user);
        String token = jwtUtil.generateToken(
                user.getId(),
                user.getEmail()
        );

        return new AuthResponse(
            user.getId(),
            user.getEmail(),
            token
        );
    }

    // ---------------- LOGIN ----------------
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() ->
                new RuntimeException("Invalid credentials")
            );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(
                user.getId(),
                user.getEmail()
        );

        return new AuthResponse(
            user.getId(),
            user.getEmail(),
            token
        );
    }
}
