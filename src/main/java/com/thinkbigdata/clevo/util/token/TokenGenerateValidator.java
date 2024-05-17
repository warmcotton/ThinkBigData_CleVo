package com.thinkbigdata.clevo.util.token;

import com.thinkbigdata.clevo.dto.TokenDto;
import com.thinkbigdata.clevo.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class TokenGenerateValidator {
    private final UserDetailsService userDetailsService;
    @Value("${token.access}") private Long accessExpired;
    @Value("${token.refresh}") private Long refreshExpired;
    @Value("${jwt.secret}") private String secret;
    private byte[] bytes;
    private Key key;

    @PostConstruct
    private void init() {
        this.bytes = Base64.getDecoder().decode(secret);
        this.key = Keys.hmacShaKeyFor(bytes);
    }
    public TokenDto generateToken(User user) {
        Date date = Timestamp.valueOf(LocalDateTime.now());

        String access = Jwts.builder().setSubject(user.getEmail())
                .setExpiration(new Date(date.getTime() + accessExpired))
                .setIssuedAt(date)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refresh = Jwts.builder().setSubject(user.getEmail())
                .setExpiration(new Date(date.getTime() + refreshExpired))
                .setIssuedAt(date)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        TokenDto token = new TokenDto();
        token.setAccess(access);
        token.setRefresh(refresh);
        return token;
    }

    public void validateToken(String token) {
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    public Authentication createAuthentication(String token) {
        String email = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
        UserDetails userDetail =  userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetail, "", userDetail.getAuthorities());
    }
}
