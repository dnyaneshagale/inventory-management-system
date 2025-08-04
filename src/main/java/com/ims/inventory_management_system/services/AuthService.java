package com.ims.inventory_management_system.services;

import com.ims.inventory_management_system.dto.AuthRequestDto;
import com.ims.inventory_management_system.dto.AuthResponseDto;

public interface AuthService {
    AuthResponseDto authenticate(AuthRequestDto authRequest);
}