package com.bikash.LinkSnap.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${backend.url}")
    private String backendUrl;

    public void sendVerificationEmail(
            String username,
            String email,
            String token) {

        String verificationUrl =
                backendUrl + "/auth/verify?token="
                        + token;

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("Verify Your LinkSnap Account");

        message.setText(
                "Hello " + username + ",\n\n" +

                        "Welcome to LinkSnap 🚀\n\n" +

                        "Thank you for creating your account.\n\n" +

                        "To activate your account, please verify your email address by clicking the link below:\n\n" +

                        verificationUrl + "\n\n" +

                        "This verification link will expire in 24 hours.\n\n" +

                        "If you did not create this account, you can safely ignore this email.\n\n" +

                        "Features available after verification:\n" +
                        "• Create Short Links\n" +
                        "• Generate QR Codes\n" +
                        "• Track Link Analytics\n" +
                        "• Manage Your Links\n\n" +

                        "Thank you for choosing LinkSnap.\n\n" +

                        "Best Regards,\n" +
                        "LinkSnap Team"
        );

        mailSender.send(message);
    }
}