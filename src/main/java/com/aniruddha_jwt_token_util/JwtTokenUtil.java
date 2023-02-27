package com.aniruddha_jwt_token_util;
import java.util.List;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private int expiration;

    public String generateToken(Authentication authentication) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", authentication.getName());
        claims.put("authorities", authentication.getAuthorities());

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration * 1000);

        return ((Object) ((JwtBuilder) Jwts.builder())
        	    .setClaims(claims))
        	    .setIssuedAt(now)
        	    .setExpiration(expirationDate)
        	    .signWith(SignatureAlgorithm.HS512, Base64.getEncoder().encodeToString(secret.getBytes()))
        	    .compact();

    }

    public String getUsernameFromToken(String token) {
        Claims claims = ((JwtParser) Jwts.parser())
                .setSigningKey(Base64.getEncoder().encodeToString(secret.getBytes()))
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }



    public List<SimpleGrantedAuthority> getAuthoritiesFromToken(String token) {
        Claims claims = ((Object) ((JwtParser) Jwts.parser())
                .setSigningKey(Base64.getEncoder().encodeToString(secret.getBytes())))
                .parseClaimsJws(token)
                .getBody();

        List<? extends Map<String, ?>> authorities = claims.get("authorities", List.class);

        return authorities.stream()
                .map(authority -> new SimpleGrantedAuthority((String) authority.get("authority")))
                .collect(Collectors.toList());
    }


    public boolean validateToken(String token) {
        try {
            ((Object) ((JwtParser) Jwts.parserBuilder()).setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

    

