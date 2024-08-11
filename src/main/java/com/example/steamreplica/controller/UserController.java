package com.example.steamreplica.controller;

import com.example.steamreplica.model.userApplication.User;
import com.example.steamreplica.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    
    public List<User> getAllUsers() {
        return new ArrayList<>();
    }

    public User getUserById(long id) {
        return null;
    }
}
