package com.example.steamreplica.Auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
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
}
