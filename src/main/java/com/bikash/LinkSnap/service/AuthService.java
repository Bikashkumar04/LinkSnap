package com.bikash.LinkSnap.service;

import com.bikash.LinkSnap.dto.AuthResponseDTO;
import com.bikash.LinkSnap.dto.LoginRequestDTO;
import com.bikash.LinkSnap.dto.RefreshTokenRequestDTO;
import com.bikash.LinkSnap.dto.RegisterRequestDTO;

public interface AuthService {

    AuthResponseDTO register(RegisterRequestDTO request);

    AuthResponseDTO login(LoginRequestDTO request);

    AuthResponseDTO refreshToken(RefreshTokenRequestDTO request);

    void logout(RefreshTokenRequestDTO request);

    boolean validateAccessToken(String accessToken);

    Long extractUserId(String accessToken);
}
