package com.bikash.LinkSnap.security;

import com.bikash.LinkSnap.dto.AuthResponse;
import com.bikash.LinkSnap.dto.LoginRequest;
import com.bikash.LinkSnap.dto.RegisterRequest;
import com.bikash.LinkSnap.entity.User;
import com.bikash.LinkSnap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public void register(RegisterRequest request) {

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(
                        passwordEncoder.encode(request.getPassword())
                )
                .role("USER")
                .build();

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getIdentifier(),
                        request.getPassword()
                )
        );

        User user = userRepository
                .findByEmailOrUsername(
                        request.getIdentifier(),
                        request.getIdentifier()
                )
                .orElseThrow();

        String token =
                jwtService.generateToken(user.getEmail());

        return new AuthResponse(token);
    }
}
