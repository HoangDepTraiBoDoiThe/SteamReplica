package com.example.steamreplica.repository;

import com.example.steamreplica.model.auth.AuthUserDetail;
import com.example.steamreplica.model.userApplication.ApplicationRole;
import com.example.steamreplica.model.userApplication.User;
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
        AuthUserDetail userDetails = user.toAuthUserDetail();
        return userDetails;
    }
}
