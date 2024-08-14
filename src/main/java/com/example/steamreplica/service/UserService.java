package com.example.steamreplica.service;

import com.example.steamreplica.dtos.request.RegisterRequest;
import com.example.steamreplica.model.purchasedLibrary.BoughtLibrary;
import com.example.steamreplica.model.userApplication.ApplicationRole;
import com.example.steamreplica.model.userApplication.User;
import com.example.steamreplica.repository.BoughtLibraryRepository;
import com.example.steamreplica.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BoughtLibraryRepository boughtLibraryRepository;
    private final RoleService roleService;

    public Collection<User> findUsersWithByRole(String roleName) {
        return userRepository.findUsersByRoleName(roleName);
    }

    @Transactional
    public User createNewUserWithRoles(RegisterRequest request, Set<ApplicationRole> roles) {
        if (userRepository.findUserByEmail(request.getEmail()).isEmpty()) {
            User user = request.toUser(passwordEncoder);
            user.setRoles(roles);
            user.setBoughtLibrary(new BoughtLibrary());
            return userRepository.save(user);
        } else {
            throw new RuntimeException("User with this email already exists");
        }
    }
}
