package com.djembe.app.service;

import com.djembe.app.dto.AuthResponse;
import com.djembe.app.dto.LoginRequest;
import com.djembe.app.dto.RegisterRequest;
import com.djembe.app.model.User;
import com.djembe.app.repository.UserRepository;
import com.djembe.app.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        User saved = userRepository.save(user);

        String token = jwtUtil.generateToken(saved.getId(), saved.getEmail());
        return new AuthResponse(token, saved.getId(), saved.getUsername(), saved.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getEmail());
    }
}
