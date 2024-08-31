package com.example.steamreplica.controller;

import com.example.steamreplica.dtos.response.user.UserResponse_Full;
import com.example.steamreplica.dtos.response.user.UserResponse_Minimal;
import com.example.steamreplica.model.userApplication.User;
import com.example.steamreplica.service.UserService;
import com.example.steamreplica.service.exception.AuthenticationException;
import com.example.steamreplica.util.MyPermissionEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final MyPermissionEvaluator myPermissionEvaluator;
    
    public List<User> getAllUsers() {
        return new ArrayList<>();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponse_Full>> getUserById(@PathVariable long id, Authentication authentication) {
        return ResponseEntity.ok(userService.findUsersWithById(id, authentication));
    }

    @PostMapping("/{id}/become-dev")
    public ResponseEntity<EntityModel<UserResponse_Minimal>> requestToBecomeDev(@PathVariable long id, Authentication authentication) {
        if (myPermissionEvaluator.checkOwnerRequest(authentication, id))
            return ResponseEntity.ok(userService.RequestToBecomeDev(id, authentication));
        else throw new AuthenticationException("Client is not the owner of the data");
    }

    @PostMapping("/{id}/become-publisher")
    public ResponseEntity<EntityModel<UserResponse_Minimal>> requestToBecomePublisher(@PathVariable long id, Authentication authentication) {
        if (myPermissionEvaluator.checkOwnerRequest(authentication, id))
            return ResponseEntity.ok(userService.RequestToBecomePublisher(id, authentication));
        else throw new AuthenticationException("Client is not the owner of the data");
    }
}
