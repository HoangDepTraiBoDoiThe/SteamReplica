package com.example.steamreplica.util;

import com.example.steamreplica.constants.SystemRole;
import com.example.steamreplica.dtos.auth.RegisterRequest;
import com.example.steamreplica.model.userApplication.ApplicationRole;
import com.example.steamreplica.service.RoleService;
import com.example.steamreplica.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer {
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${auth.dataInit.aminDefaultPhoneNumb}")
    private String adminInitPhoneNumber;
    @Value("${auth.dataInit.aminDefaultEmail}")
    private String aminDefaultEmail;
    @Value("${auth.dataInit.aminDefaultPassword}")
    private String aminDefaultPassword;
    
    @PostConstruct
    public void initData() {
        if (roleService.getAllRoles().isEmpty()) initRole();
        if (userService.findUsersWithByRole(SystemRole.ADMIN.name()).isEmpty()) initAdmin();
    }

    void initRole() {
        Collection<ApplicationRole> roles = Arrays.stream(SystemRole.values()).map(systemRole -> new ApplicationRole(systemRole.name())).toList();
        roleService.createRoles(roles);
    }
    void initAdmin() {
        RegisterRequest request = new RegisterRequest(aminDefaultEmail, aminDefaultPassword, "Admin", adminInitPhoneNumber);
        Set<ApplicationRole> roles = new HashSet<>();
        ApplicationRole role = roleService.getApplicationRoleByName(SystemRole.ADMIN.name());
        roles.add(role);
        userService.createNewUserWithRoles(request, roles);
    }
}
