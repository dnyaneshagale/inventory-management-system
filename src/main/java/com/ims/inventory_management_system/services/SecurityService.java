package com.ims.inventory_management_system.services;

public interface SecurityService {
    boolean isCurrentUser(Long userId);
}