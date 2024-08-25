package com.example.steamreplica.service;

import com.example.steamreplica.dtos.request.RegisterRequest;
import com.example.steamreplica.dtos.response.user.UserResponse_Full;
import com.example.steamreplica.dtos.response.user.UserResponse_Minimal;
import com.example.steamreplica.model.purchasedLibrary.BoughtLibrary;
import com.example.steamreplica.model.purchasedLibrary.DevOwnedLibrary;
import com.example.steamreplica.model.userApplication.ApplicationRole;
import com.example.steamreplica.model.userApplication.User;
import com.example.steamreplica.repository.UserRepository;
import com.example.steamreplica.util.ServiceHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ServiceHelper serviceHelper;
    private final PasswordEncoder passwordEncoder;

    public Collection<User> findUsersWithByRole(String roleName) {
        return userRepository.findUsersByRoleName(roleName);
    }
    public User findUsersWithByEmail_entity(String email) {
        return userRepository.findUserByEmail(email).orElseThrow(() -> new RuntimeException(String.format("User with email %s not found", email)));
    }
    public User findUsersWithById_entity(long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException(String.format("User with id [%d] not found", id)));
    }
    
    @Transactional
    public User findUsersWithById_entityFull(long id) {
        return userRepository.findById_full(id).orElseThrow(() -> new RuntimeException(String.format("User with id [%d] not found", id)));
    }
    
    public EntityModel<UserResponse_Full> findUsersWithById(long id, Authentication authentication) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException(String.format("User with id [%d] not found", id)));
        return serviceHelper.makeUserResponse(UserResponse_Full.class, user, authentication, "");
    }

    @Transactional
    public EntityModel<UserResponse_Minimal> RequestToBecomeDev(long id, Authentication authentication) {
        User user = findUsersWithById_entity(id);
        if (user.getDevOwnedLibrary() != null) throw new RuntimeException("User is already a developer");

        DevOwnedLibrary devOwnedLibrary = new DevOwnedLibrary(user);
        user.setDevOwnedLibrary(devOwnedLibrary);
        User updatedUser = userRepository.save(user);
        return serviceHelper.makeUserResponse(UserResponse_Minimal.class, updatedUser, authentication, "User is now a developer");
    }

    @Transactional
    public EntityModel<UserResponse_Minimal> RequestToBecomePublisher(long id, Authentication authentication) {
        User user = findUsersWithById_entity(id);
        if (user.getPublisherOwnedLibrary() != null) throw new RuntimeException("User is already a publisher");

        DevOwnedLibrary devOwnedLibrary = new DevOwnedLibrary(user);
        user.setDevOwnedLibrary(devOwnedLibrary);
        return serviceHelper.makeUserResponse(UserResponse_Minimal.class, userRepository.save(user), authentication, "User is now a publisher");
    }

    @Transactional
    public User createNewUserWithRoles(RegisterRequest request, Set<ApplicationRole> roles) {
        if (userRepository.findUserByEmail(request.getEmail()).isEmpty()) {
            User user = request.toUser(passwordEncoder);
            user.setRoles(roles);
            User newCreatedUser = userRepository.save(user);
            newCreatedUser.setBoughtLibrary(new BoughtLibrary(newCreatedUser));
            return userRepository.save(newCreatedUser);
        } else {
            throw new RuntimeException("User with this email already exists");
        }
    }
}
