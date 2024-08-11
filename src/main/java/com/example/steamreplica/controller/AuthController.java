package com.example.steamreplica.controller;

import com.example.steamreplica.Auth.JwtAuthUtil;
import com.example.steamreplica.constants.SystemRole;
import com.example.steamreplica.dtos.auth.LoginRequest;
import com.example.steamreplica.dtos.auth.LoginResponse;
import com.example.steamreplica.dtos.auth.RegisterRequest;
import com.example.steamreplica.dtos.auth.RegisterResponse;
import com.example.steamreplica.model.auth.AuthUserDetail;
import com.example.steamreplica.model.userApplication.ApplicationRole;
import com.example.steamreplica.repository.AuthUserDetailService;
import com.example.steamreplica.service.RoleService;
import com.example.steamreplica.service.UserService;
import com.example.steamreplica.util.StaticHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final RoleService roleService;
    private final AuthUserDetailService authUserDetailService;
    private final JwtAuthUtil authUtil;

    @PostMapping(("/login"))
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest, BindingResult result) {
        var errors = StaticHelper.extractBindingErrorMessages(result);
        if (!errors.isEmpty()) ResponseEntity.badRequest().body(errors);
        
        AuthUserDetail userDetail = (AuthUserDetail) authUserDetailService.loadUserByUsername(loginRequest.getEmail());
        String token = authUtil.generateToken(userDetail);
        LoginResponse response = new LoginResponse(token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request, BindingResult result) {
        var errors = StaticHelper.extractBindingErrorMessages(result);
        if (!errors.isEmpty()) ResponseEntity.badRequest().body(errors);
        
        Set<ApplicationRole> roles = new HashSet<>();
        ApplicationRole role = roleService.getApplicationRoleByName(SystemRole.ADMIN.name());
        roles.add(role);

        userService.createNewUserWithRoles(request, roles);
        return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterResponse("User created successfully"));
    }
}
