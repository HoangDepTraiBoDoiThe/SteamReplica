package com.example.steamreplica.repository.auth;

import com.example.steamreplica.model.auth.AuthUserDetail;
import com.example.steamreplica.model.userApplication.ApplicationRole;
import com.example.steamreplica.model.userApplication.User;
import com.example.steamreplica.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String emailAsUsername = username;
        User user = userRepository.findUserByEmail(emailAsUsername).orElseThrow(() -> new UsernameNotFoundException(String.format("User with [%s] emil not found", emailAsUsername)));
        AuthUserDetail userDetails = new AuthUserDetail();
        userDetails.setUsername(user.getUserName());
        userDetails.setPassword(user.getPassword());
        userDetails.setRoles(user.getRoles().stream().map(ApplicationRole::getRoleName).collect(Collectors.toList()));
        return userDetails;
    }

    public User createNewUser(User user) {
        if (userRepository.findUserByEmail(user.getEmail()).isEmpty()) {
            User newCreatedUser = userRepository.save(user);
            return newCreatedUser;
        } else {
            throw new RuntimeException("User with this email already exists");
        } 
    }
}
