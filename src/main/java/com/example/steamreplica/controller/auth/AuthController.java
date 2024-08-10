package com.example.steamreplica.controller.auth;

import com.example.steamreplica.Auth.JwtAuthUtil;
import com.example.steamreplica.dtos.auth.LoginRequest;
import com.example.steamreplica.dtos.auth.LoginResponse;
import com.example.steamreplica.dtos.auth.RegisterRequest;
import com.example.steamreplica.dtos.auth.RegisterResponse;
import com.example.steamreplica.model.auth.AuthUserDetail;
import com.example.steamreplica.model.userApplication.User;
import com.example.steamreplica.repository.auth.AuthUserDetailService;
import com.example.steamreplica.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthUserDetailService authUserDetailService;
    private final UserRepository userRepository;
    private final JwtAuthUtil authUtil;

    @PostMapping(("/login"))
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        AuthUserDetail userDetail = (AuthUserDetail) authUserDetailService.loadUserByUsername(loginRequest.getEmail());
        String token = authUtil.generateToken(userDetail);
        LoginResponse response = new LoginResponse(token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reqister")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        User newUser = new User(request.getUsername(), request.getPhoneNumber(), request.getEmail(), request.getPassword());
        userRepository.save(newUser);
        return ResponseEntity.ok(new RegisterResponse("User created successfully"));
    }
}
