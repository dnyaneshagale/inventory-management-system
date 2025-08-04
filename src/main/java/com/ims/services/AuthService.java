package com.ims.services;

import com.ims.dto.AuthRequestDto;
import com.ims.dto.AuthResponseDto;

public interface AuthService {
    AuthResponseDto authenticate(AuthRequestDto authRequest);
}