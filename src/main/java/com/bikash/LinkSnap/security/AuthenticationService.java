package com.bikash.LinkSnap.security;

import com.bikash.LinkSnap.dto.AuthResponse;
import com.bikash.LinkSnap.dto.LoginRequest;
import com.bikash.LinkSnap.dto.RegisterRequest;
import com.bikash.LinkSnap.entity.User;
import com.bikash.LinkSnap.entity.VerificationToken;
import com.bikash.LinkSnap.repository.UserRepository;
import com.bikash.LinkSnap.repository.VerificationTokenRepository;
import com.bikash.LinkSnap.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService;

    public void register(RegisterRequest request) {

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(
                        passwordEncoder.encode(request.getPassword())
                )
                .role("USER")
                .enabled(false)
                .build();

        userRepository.save(user);

        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken =
                new VerificationToken();

        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(
                LocalDateTime.now().plusHours(24)
        );

        verificationTokenRepository.save(
                verificationToken
        );

        emailService.sendVerificationEmail(
                user.getUsername(),
                user.getEmail(),
                token
        );
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmailOrUsername(
                        request.getIdentifier(),
                        request.getIdentifier()
                )
                .orElseThrow();

        if (!user.isEnabled()) {
            throw new RuntimeException(
                    "Please verify your email first"
            );
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getIdentifier(),
                        request.getPassword()
                )
        );

        String token =
                jwtService.generateToken(user.getEmail());

        return new AuthResponse(token);
    }


    //This method will be used to get the currently authenticated user in the application. It retrieves the username from the security context and then fetches the corresponding user from the database using the UserRepository.
    public User getCurrentUser() {

        String Username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        return userRepository.findByEmailOrUsername(Username, Username)
                .orElseThrow();
    }


    public void verifyEmail(String token) {

        VerificationToken verificationToken =
                verificationTokenRepository
                        .findByToken(token)
                        .orElseThrow(
                                () -> new RuntimeException("Invalid token")
                        );

        if (verificationToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException("Token expired");
        }

        User user = verificationToken.getUser();

        user.setEnabled(true);

        userRepository.save(user);

        verificationTokenRepository.delete(
                verificationToken
        );
    }
}
