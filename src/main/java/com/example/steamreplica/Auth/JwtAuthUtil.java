package com.example.steamreplica.Auth;

import com.example.steamreplica.model.auth.AuthUserDetail;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Configuration
public class JwtAuthUtil {
    @Value("${auth.security.token.expirationInMillis}")
    private String expirationInMillis;
    @Value("${auth.security.token.securityKey}")
    private String securityKey;
    
    public <R> R extractClaimsProperty(String token, Function<Claims, R> claimsRFunction) {
        var claims = Jwts.parserBuilder().setSigningKey(securityKey).build().parseClaimsJws(token).getBody();
        return claimsRFunction.apply(claims);
    }

    public boolean isTokenValid(String token) {
        return extractClaimsProperty(token, Claims::getExpiration).before(new Date());
    }

    public String generateToken(AuthUserDetail user) {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("user name", user.getUsername());
        claims.put("roles", user.getAuthorities());
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, securityKey)
                .setSubject(user.getEmail())
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis() * Long.parseLong(expirationInMillis) * 1000))
                .compact();
    }
}
