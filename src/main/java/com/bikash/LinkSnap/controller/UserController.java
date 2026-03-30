package com.bikash.LinkSnap.controller;

import com.bikash.LinkSnap.dto.ChangePasswordRequestDTO;
import com.bikash.LinkSnap.dto.UpdateProfileRequestDTO;
import com.bikash.LinkSnap.dto.UserDTO;
import com.bikash.LinkSnap.entity.User;
import com.bikash.LinkSnap.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMe(Authentication authentication) {
        return ResponseEntity.ok(userService.getUserById(currentUserId(authentication)));
    }

    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateProfile(
            @Valid @RequestBody UpdateProfileRequestDTO request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(userService.updateProfile(currentUserId(authentication), request.getName()));
    }

    @PostMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequestDTO request,
            Authentication authentication
    ) {
        userService.changePassword(currentUserId(authentication), request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.noContent().build();
    }

    private Long currentUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return user.getId();
    }
}
