package com.thinkbigdata.clevo.filter;

import com.thinkbigdata.clevo.util.token.TokenGenerateValidator;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final TokenGenerateValidator tokenGenerateValidator;
    private final RedisTemplate<String, String> redisTemplate;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = getToken(request.getHeader("Authorization"));

        if (accessToken != null) {
            try {
                tokenGenerateValidator.validateToken(accessToken);
            } catch (MalformedJwtException | IllegalArgumentException | UnsupportedJwtException | SignatureException e) {
                response.sendError(401, "Invalid JWT Token");
                return;
            } catch (ExpiredJwtException e) {
                response.sendError(401, "Token Expired");
                return;
            }
            if (redisTemplate.opsForValue().get("logout:"+accessToken) == null) {
                Authentication authentication = tokenGenerateValidator.createAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer")) {
            token = token.substring(7);
        } else token = null;
        return token;
    }
}
