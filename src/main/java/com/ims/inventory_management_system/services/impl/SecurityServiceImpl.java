package com.ims.inventory_management_system.services.impl;

import com.ims.inventory_management_system.repositories.UserRepository;
import com.ims.inventory_management_system.services.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final UserRepository userRepository;

    @Override
    public boolean isCurrentUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        
        return userRepository.findById(userId)
                .map(user -> user.getUsername().equals(currentUsername))
                .orElse(false);
    }
}