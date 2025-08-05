package com.ims.inventory_management_system.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private Boolean active;
    private Set<String> roles = new HashSet<>();

    @NotNull
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
}