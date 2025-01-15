package com.favoriteplace.global.auth.filter;

import com.favoriteplace.global.auth.JwtAuthenticationNeededPath;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.auth.provider.JwtTokenProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import static com.favoriteplace.global.exception.ErrorCode.INVALID_BEARER_PREFIX;
import static com.favoriteplace.global.exception.ErrorCode.MISSING_AUTHORIZATION_JWT;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate redisTemplate;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getServletPath();
        String method = request.getMethod();

        return !JwtAuthenticationNeededPath.NEEDED_JWT_AUTHENTICATION_PATHS.stream()
                .anyMatch(excludePath -> excludePath.matches(requestURI, method));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = getJwtFromHeader(request);

        if ("websocket".equalsIgnoreCase(request.getHeader("Upgrade"))) {
            chain.doFilter(request, response);
            return;
        }

        jwtTokenProvider.validateToken(token);

        /*1. Redis에 해당 accessToken logout 여부 확인 */
        String isLogout = (String) redisTemplate.opsForValue().get(token);

        if ("logout".equals(isLogout)) {
            throw new RestApiException(ErrorCode.TOKEN_NOT_VALID);
        } else {
            /* 2. 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext에 저장 */
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), request.getRequestURI());
        }

        chain.doFilter(request, response);
    }

    private String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (!StringUtils.hasText(bearerToken)) {
            throw new RestApiException(MISSING_AUTHORIZATION_JWT);
        } else if (!bearerToken.startsWith("Bearer")) {
            throw new RestApiException(INVALID_BEARER_PREFIX);
        }
        return bearerToken.substring(7);
    }

}