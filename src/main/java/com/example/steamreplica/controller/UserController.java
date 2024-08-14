package com.example.steamreplica.controller;

import com.example.steamreplica.model.userApplication.User;
import com.example.steamreplica.service.GameService;
import com.example.steamreplica.service.UserService;
import com.example.steamreplica.util.StaticHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public User getUserById(@PathVariable long id) {
        return null;
    }

    @GetMapping("{id}/games")
    public ResponseEntity<?> getGamesByOwnerId(@PathVariable String id, Authentication authentication) {
        Collection<String> roles = StaticHelper.extractGrantedAuthority(authentication);
        return ResponseEntity.ok(gameService.getAllGames(authentication));
    }

}
