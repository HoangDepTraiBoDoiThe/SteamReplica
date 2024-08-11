package com.example.steamreplica.service;

import com.example.steamreplica.constants.SystemRole;
import com.example.steamreplica.model.userApplication.ApplicationRole;
import com.example.steamreplica.model.userApplication.User;
import com.example.steamreplica.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;

    public Collection<User> findUsersWithByRole(String roleName) {
        return userRepository.findUsersByRoleName(roleName);
    }

    public User createNewUserWithRoles(User user, Set<ApplicationRole> roles) {
        if (userRepository.findUserByEmail(user.getEmail()).isEmpty()) {
            user.setRoles(roles);
            return  userRepository.save(user);
        } else {
            throw new RuntimeException("User with this email already exists");
        }
    }
}
