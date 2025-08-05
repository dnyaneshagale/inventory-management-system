package com.ims.inventory_management_system.config;

import com.ims.inventory_management_system.entities.Role;
import com.ims.inventory_management_system.entities.User;
import com.ims.inventory_management_system.repositories.RoleRepository;
import com.ims.inventory_management_system.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Profile("!test") // Don't run during tests
public class DatabaseInitializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Environment env;  // To read config properties

    @PostConstruct
    @Transactional
    public void initialize() {
        if (roleRepository.count() == 0) {
            createRoles();
        }

        if (userRepository.count() == 0) {
            createAdminUser();
        }
    }

    private void createRoles() {
        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        adminRole.setDescription("Administrator role with full access");
        roleRepository.save(adminRole);

        Role managerRole = new Role();
        managerRole.setName("ROLE_MANAGER");
        managerRole.setDescription("Manager role with department-level access");
        roleRepository.save(managerRole);

        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        userRole.setDescription("Standard user role with limited access");
        roleRepository.save(userRole);
    }

    private void createAdminUser() {
        User adminUser = new User();
        adminUser.setUsername("admin");

        String adminPassword = env.getProperty("admin.password", "admin123");
        adminUser.setPassword(passwordEncoder.encode(adminPassword));

        adminUser.setFullName("System Administrator");
        adminUser.setEmail("admin@example.com");
        adminUser.setActive(true);

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("Admin role not found"));

        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);
        adminUser.setRoles(roles);

        userRepository.save(adminUser);
    }
}
