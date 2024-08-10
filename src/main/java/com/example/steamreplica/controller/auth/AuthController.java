package com.example.steamreplica.controller.auth;

import com.example.steamreplica.Auth.JwtAuthUtil;
import com.example.steamreplica.constants.SystemRole;
import com.example.steamreplica.dtos.auth.LoginRequest;
import com.example.steamreplica.dtos.auth.LoginResponse;
import com.example.steamreplica.dtos.auth.RegisterRequest;
import com.example.steamreplica.dtos.auth.RegisterResponse;
import com.example.steamreplica.model.auth.AuthUserDetail;
import com.example.steamreplica.model.userApplication.ApplicationRole;
import com.example.steamreplica.model.userApplication.User;
import com.example.steamreplica.repository.auth.AuthUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthUserDetailService authUserDetailService;
    private final JwtAuthUtil authUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping(("/login"))
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        AuthUserDetail userDetail = (AuthUserDetail) authUserDetailService.loadUserByUsername(loginRequest.getEmail());
        String token = authUtil.generateToken(userDetail);
        LoginResponse response = new LoginResponse(token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        User newUser = request.toUser(passwordEncoder);

        ApplicationRole role = new ApplicationRole(SystemRole.GAMER.name());
        role.getUsers().add(newUser);

        authUserDetailService.createNewUser(newUser);
        return ResponseEntity.ok(new RegisterResponse("User created successfully"));
    }
}
