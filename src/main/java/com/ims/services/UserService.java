package com.ims.services;

import com.ims.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto, String password);
    UserDto getUserById(Long id);
    UserDto getUserByUsername(String username);
    List<UserDto> getAllUsers();
    UserDto updateUser(Long id, UserDto userDto);
    void changePassword(Long id, String currentPassword, String newPassword);
    void assignRolesToUser(Long userId, List<String> roleNames);
    void deleteUser(Long id);
}