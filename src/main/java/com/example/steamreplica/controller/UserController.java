package com.example.steamreplica.controller;

import com.example.steamreplica.dtos.response.BaseResponse;
import com.example.steamreplica.dtos.response.user.UserResponse_Full;
import com.example.steamreplica.model.userApplication.User;
import com.example.steamreplica.service.GameService;
import com.example.steamreplica.service.UserService;
import com.example.steamreplica.util.StaticHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final GameService gameService;
    
    public List<User> getAllUsers() {
        return new ArrayList<>();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponse_Full>> getUserById(@PathVariable long id, Authentication authentication) {
        return ResponseEntity.ok(userService.findUsersWithById(id, authentication));
    }

    @PostMapping("/{id}/become-dev")
    public ResponseEntity<EntityModel<BaseResponse>> requestToBecomeDev(@PathVariable long id, Authentication authentication) {
        return ResponseEntity.ok(userService.RequestToBecomeDev(id, authentication));
    }

    @PostMapping("/{id}/become-publisher")
    public ResponseEntity<EntityModel<BaseResponse>> requestToBecomePublisher(@PathVariable long id, Authentication authentication) {
        return ResponseEntity.ok(userService.RequestToBecomePublisher(id, authentication));
    }

    @GetMapping("{id}/games")
    public ResponseEntity<?> getGamesByOwnerId(@PathVariable String id, Authentication authentication) {
        Collection<String> roles = StaticHelper.extractGrantedAuthority(authentication);
        return ResponseEntity.ok(gameService.getAllGames(authentication));
    }

}
