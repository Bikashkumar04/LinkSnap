package com.bikash.LinkSnap.service;

import com.bikash.LinkSnap.dto.UserDTO;

public interface UserService {

    UserDTO getUserById(Long userId);

    UserDTO updateProfile(Long userId, String name);

    void changePassword(Long userId, String currentPassword, String newPassword);

    UserDTO updateStatus(Long userId, String status);
}
