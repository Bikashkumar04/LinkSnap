package com.bikash.LinkSnap.service.impl;

import com.bikash.LinkSnap.dto.AuthResponseDTO;
import com.bikash.LinkSnap.dto.LoginRequestDTO;
import com.bikash.LinkSnap.dto.RefreshTokenRequestDTO;
import com.bikash.LinkSnap.dto.RegisterRequestDTO;
import com.bikash.LinkSnap.entity.RefreshToken;
import com.bikash.LinkSnap.entity.User;
import com.bikash.LinkSnap.repository.RefreshTokenRepository;
import com.bikash.LinkSnap.repository.UserRepository;
import com.bikash.LinkSnap.service.AuthService;
import com.bikash.LinkSnap.service.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        String normalizedEmail = normalizeEmail(request.getEmail());

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setStatus("ACTIVE");

        User savedUser = userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(savedUser.getId(), savedUser.getEmail());
        String refreshToken = jwtService.generateRefreshToken(savedUser.getId());
        saveRefreshToken(savedUser.getId(), refreshToken);

        return new AuthResponseDTO(
                savedUser.getId(),
                savedUser.getEmail(),
                accessToken,
                refreshToken
        );
    }

    @Override
    @Transactional
    public AuthResponseDTO login(LoginRequestDTO request) {
        String normalizedEmail = normalizeEmail(request.getEmail());

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new IllegalStateException("User account is not active");
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getId());
        saveRefreshToken(user.getId(), refreshToken);

        return new AuthResponseDTO(
                user.getId(),
                user.getEmail(),
                accessToken,
                refreshToken
        );
    }

    @Override
    @Transactional
    public AuthResponseDTO refreshToken(RefreshTokenRequestDTO request) {
        RefreshToken existingRefreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (existingRefreshToken.isRevoked()) {
            throw new IllegalArgumentException("Refresh token is revoked");
        }

        if (existingRefreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Refresh token has expired");
        }

        if (!jwtService.isTokenValid(existingRefreshToken.getToken())) {
            throw new IllegalArgumentException("Refresh token is invalid");
        }

        Long userId = jwtService.extractUserId(existingRefreshToken.getToken());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        existingRefreshToken.setRevoked(true);
        refreshTokenRepository.save(existingRefreshToken);

        String newAccessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());
        String newRefreshToken = jwtService.generateRefreshToken(user.getId());
        saveRefreshToken(user.getId(), newRefreshToken);

        return new AuthResponseDTO(
                user.getId(),
                user.getEmail(),
                newAccessToken,
                newRefreshToken
        );
    }

    @Override
    @Transactional
    public void logout(RefreshTokenRequestDTO request) {
        refreshTokenRepository.findByToken(request.getRefreshToken())
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }

    @Override
    public boolean validateAccessToken(String accessToken) {
        return jwtService.isTokenValid(accessToken);
    }

    @Override
    public Long extractUserId(String accessToken) {
        return jwtService.extractUserId(accessToken);
    }

    private void saveRefreshToken(Long userId, String token) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(userId);
        refreshToken.setToken(token);
        refreshToken.setExpiresAt(toLocalDateTime(jwtService.extractExpiration(token)));
        refreshToken.setRevoked(false);
        refreshTokenRepository.save(refreshToken);
    }

    private LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
