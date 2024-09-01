package com.example.steamreplica.util;

import com.example.steamreplica.model.auth.AuthUserDetail;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Configuration
public class JwtAuthUtil {
    @Value("${auth.security.token.expirationMinutes}")
    private int expirationMinutes;
    @Value("${auth.security.token.securityKey}")
    private String securityKey;
    public <R> R extractClaimsProperty(String token, Function<Claims, R> claimsRFunction) {
        var claims = Jwts.parserBuilder().setSigningKey(securityKey).build().parseClaimsJws(token).getBody();
        return claimsRFunction.apply(claims);
    }

    public boolean isTokenValid(String token) {
        return extractClaimsProperty(token, Claims::getExpiration).after(new Date());
    }

    public String generateToken(AuthUserDetail user) {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("user name", user.getUsername());
        claims.put("roles", user.getAuthorities());
        claims.put("id", user.getId());
        Instant now = Instant.now();
        Instant expiryDate = now.plus(expirationMinutes, ChronoUnit.MINUTES);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiryDate))
                .signWith(getSecurityKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private SecretKey getSecurityKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(securityKey));
    }
}
