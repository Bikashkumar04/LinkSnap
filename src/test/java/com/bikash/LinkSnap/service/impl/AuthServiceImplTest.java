package com.bikash.LinkSnap.service.impl;

import com.bikash.LinkSnap.dto.AuthResponseDTO;
import com.bikash.LinkSnap.dto.LoginRequestDTO;
import com.bikash.LinkSnap.dto.RegisterRequestDTO;
import com.bikash.LinkSnap.entity.User;
import com.bikash.LinkSnap.repository.RefreshTokenRepository;
import com.bikash.LinkSnap.repository.UserRepository;
import com.bikash.LinkSnap.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void registerShouldCreateUserAndReturnTokens() {
        RegisterRequestDTO request = new RegisterRequestDTO("Bikash", "bikash@mail.com", "strongPass123");

        when(userRepository.existsByEmail("bikash@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("strongPass123")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(11L);
            return user;
        });
        when(jwtService.generateAccessToken(11L, "bikash@mail.com")).thenReturn("access-token");
        when(jwtService.generateRefreshToken(11L)).thenReturn("refresh-token");
        when(jwtService.extractExpiration("refresh-token")).thenReturn(new Date(System.currentTimeMillis() + 60000));

        AuthResponseDTO response = authService.register(request);

        assertEquals(11L, response.getUserId());
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        verify(refreshTokenRepository, times(1)).save(any());
    }

    @Test
    void loginShouldFailForInvalidPassword() {
        LoginRequestDTO request = new LoginRequestDTO("bikash@mail.com", "wrong-pass");

        User existing = new User();
        existing.setId(11L);
        existing.setEmail("bikash@mail.com");
        existing.setPasswordHash("encodedPass");
        existing.setStatus("ACTIVE");

        when(userRepository.findByEmail("bikash@mail.com")).thenReturn(Optional.of(existing));
        when(passwordEncoder.matches("wrong-pass", "encodedPass")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> authService.login(request));
        assertEquals("Invalid email or password", ex.getMessage());
        verify(jwtService, never()).generateAccessToken(any(), any());
    }
}
