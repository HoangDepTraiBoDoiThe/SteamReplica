package com.example.steamreplica.controller;

import com.example.steamreplica.Auth.JwtAuthUtil;
import com.example.steamreplica.constants.SystemRole;
import com.example.steamreplica.dtos.request.LoginRequest;
import com.example.steamreplica.dtos.response.user.LoginResponse;
import com.example.steamreplica.dtos.request.RegisterRequest;
import com.example.steamreplica.dtos.response.user.RegisterResponse;
import com.example.steamreplica.model.auth.AuthUserDetail;
import com.example.steamreplica.model.userApplication.ApplicationRole;
import com.example.steamreplica.model.userApplication.User;
import com.example.steamreplica.repository.AuthUserDetailService;
import com.example.steamreplica.service.RoleService;
import com.example.steamreplica.service.UserService;
import com.example.steamreplica.util.StaticHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
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
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, BindingResult result) {
        var errors = StaticHelper.extractBindingErrorMessages(result);
        if (!errors.isEmpty()) return ResponseEntity.badRequest().body(errors);
        try {
            AuthUserDetail userDetail = (AuthUserDetail) authUserDetailService.loadUserByUsername(loginRequest.getEmail());
            String token = authUtil.generateToken(userDetail);
            LoginResponse response = new LoginResponse(token);

            EntityModel<LoginResponse> entityModel = EntityModel.of(response,
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserById(userDetail.getId())).withSelfRel()
            );
            
            return ResponseEntity.ok(entityModel);
        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request, BindingResult result) {
        var errors = StaticHelper.extractBindingErrorMessages(result);
        if (!errors.isEmpty()) return ResponseEntity.badRequest().body(errors);
        try {
            Set<ApplicationRole> roles = new HashSet<>();
            ApplicationRole role = roleService.getApplicationRoleByName(SystemRole.ADMIN.name());
            roles.add(role);

            User user = userService.createNewUserWithRoles(request, roles);
            RegisterResponse registerResponse = new RegisterResponse("User created successfully");
            EntityModel<RegisterResponse> entityModel = EntityModel.of(registerResponse, 
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserById(user.getId())).withSelfRel(), 
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AuthController.class).login(new LoginRequest(user.getEmail(), request.getPassword()), result)).withSelfRel()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }
}
