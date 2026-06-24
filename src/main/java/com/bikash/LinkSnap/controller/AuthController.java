package com.bikash.LinkSnap.controller;

import com.bikash.LinkSnap.dto.AuthResponse;
import com.bikash.LinkSnap.dto.LoginRequest;
import com.bikash.LinkSnap.dto.RegisterRequest;
import com.bikash.LinkSnap.security.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @Value("${frontend.url}")
    private String frontendUrl;
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody RegisterRequest request) {

        authenticationService.register(request);

        return ResponseEntity.ok("User Registered Successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                authenticationService.login(request)
        );
    }

    @GetMapping("/verify")
    public void verifyEmail(
            @RequestParam String token,
            HttpServletResponse response)
            throws IOException {

        try {

            authenticationService.verifyEmail(token);

            response.sendRedirect(
                    frontendUrl + "/email-verified"
            );

        } catch (Exception e) {

            response.sendRedirect(
                    frontendUrl + "/email-verification-failed"
            );
        }
    }
}