package com.ims.controllers;

import com.ims.dto.AuthRequestDto;
import com.ims.dto.AuthResponseDto;
import com.ims.dto.UserDto;
import com.ims.services.AuthService;
import com.ims.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> authenticateUser(@Valid @RequestBody AuthRequestDto loginRequest) {
        AuthResponseDto response = authService.authenticate(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody UserDto userDto, 
                                                @RequestParam String password) {
        UserDto createdUser = userService.createUser(userDto, password);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
}