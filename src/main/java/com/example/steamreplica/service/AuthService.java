package com.example.steamreplica.service;

import com.example.steamreplica.Auth.JwtAuthUtil;
import com.example.steamreplica.constants.HttpRequestTypes;
import com.example.steamreplica.constants.SystemRole;
import com.example.steamreplica.controller.AuthController;
import com.example.steamreplica.controller.UserController;
import com.example.steamreplica.dtos.request.LoginRequest;
import com.example.steamreplica.dtos.request.RegisterRequest;
import com.example.steamreplica.dtos.response.BaseResponse;
import com.example.steamreplica.dtos.response.user.LoginResponse;
import com.example.steamreplica.model.auth.AuthUserDetail;
import com.example.steamreplica.model.userApplication.ApplicationRole;
import com.example.steamreplica.model.userApplication.User;
import com.example.steamreplica.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final RoleService roleService;
    private final JwtAuthUtil authUtil;
    private final UserService userService;
    private final UserRepository userRepository;

    public EntityModel<BaseResponse> register(RegisterRequest registerRequest) {
        if (userRepository.findUserByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists");
        }
        
        Set<ApplicationRole> roles = new HashSet<>();
        ApplicationRole role = roleService.getApplicationRoleByName(SystemRole.GAMER.name());
        roles.add(role);

        User user = userService.createNewUserWithRoles(registerRequest, roles);
        BaseResponse registerResponse = new BaseResponse("User created successfully");
        EntityModel<BaseResponse> responseEntityModel = EntityModel.of(registerResponse,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserById(user.getId())).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AuthController.class).login(new LoginRequest(user.getEmail(), registerRequest.getPassword()), null)).withRel("Login").withType(HttpRequestTypes.POST.name())
        );

        return responseEntityModel;
    }

    public EntityModel<LoginResponse> login(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
        AuthUserDetail authUserDetail = (AuthUserDetail) authenticationManager.authenticate(authenticationToken);
        String token = authUtil.generateToken(authUserDetail);
        return EntityModel.of(new LoginResponse(authUserDetail.getId(), "Login successful", token),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserById(authUserDetail.getId())).withSelfRel().withType(HttpMethod.GET.name())
        );
    }

}
