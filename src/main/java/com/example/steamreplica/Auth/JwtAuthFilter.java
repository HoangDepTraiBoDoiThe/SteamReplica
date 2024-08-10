package com.example.steamreplica.Auth;

import com.example.steamreplica.model.auth.AuthUserDetail;
import com.example.steamreplica.model.userApplication.User;
import com.example.steamreplica.repository.auth.AuthUserDetailService;
import com.example.steamreplica.repository.user.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtAuthUtil authUtil;
    private final AuthUserDetailService authUserDetailService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractToken(request);
            if (token != null && authUtil.isTokenValid(token)) {
                String email = authUtil.extractClaimsProperty(token, Claims::getSubject);
                AuthUserDetail authUserDetail = (AuthUserDetail) authUserDetailService.loadUserByUsername(email);
                var emailPassAuth = new UsernamePasswordAuthenticationToken(authUserDetail, null, authUserDetail.getAuthorities());
                emailPassAuth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(emailPassAuth);
            }
        } catch (Exception exception) {
            logger.warn("Server message: Cannot set user authentication: ", exception.getCause());
        }
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String requestHeader = request.getHeader("Authorization");
        if (requestHeader.startsWith("Bearer ")) {
            return requestHeader.substring(7);
        }
        return null;
    }
}
