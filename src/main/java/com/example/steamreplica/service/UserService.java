package com.example.steamreplica.service;

import com.example.steamreplica.dtos.request.RegisterRequest;
import com.example.steamreplica.dtos.response.BaseResponse;
import com.example.steamreplica.dtos.response.user.UserResponse_Full;
import com.example.steamreplica.dtos.response.user.UserResponse_Minimal;
import com.example.steamreplica.model.auth.AuthUserDetail;
import com.example.steamreplica.model.purchasedLibrary.BoughtLibrary;
import com.example.steamreplica.model.purchasedLibrary.DevOwnedLibrary;
import com.example.steamreplica.model.userApplication.ApplicationRole;
import com.example.steamreplica.model.userApplication.User;
import com.example.steamreplica.repository.BoughtLibraryRepository;
import com.example.steamreplica.repository.UserRepository;
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
    
    public EntityModel<UserResponse_Full> findUsersWithById(long id, Authentication authentication) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException(String.format("User with id [%d] not found", id)));
        return serviceHelper.makeUserResponse(UserResponse_Full.class, user, authentication, "");
    }

    @Transactional
    public EntityModel<BaseResponse> RequestToBecomeDev(long id, Authentication authentication) {
        try {
            User user = findUsersWithById_entity(id);
            DevOwnedLibrary devOwnedLibrary = new DevOwnedLibrary(user);
            user.setDevOwnedLibrary(devOwnedLibrary);
            return serviceHelper.makeUserResponse(BaseResponse.class, userRepository.save(user), authentication, "User is now a developer");
        } catch (Exception e) {
            throw new RuntimeException("Unable to request to become a developer");
        }
    }

    @Transactional
    public EntityModel<BaseResponse> RequestToBecomePublisher(long id, Authentication authentication) {
        try {
            User user = findUsersWithById_entity(id);
            DevOwnedLibrary devOwnedLibrary = new DevOwnedLibrary(user);
            user.setDevOwnedLibrary(devOwnedLibrary);
            return serviceHelper.makeUserResponse(BaseResponse.class, userRepository.save(user), authentication, "User is now a publisher");
        } catch (Exception e) {
            throw new RuntimeException("Unable to request to become a developer");
        }
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
