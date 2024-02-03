package com.favoriteplace.global.security.Filter;

import com.favoriteplace.global.security.provider.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.beans.ExceptionListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final List<ExcludePath> excludePaths = Arrays.asList(
        //인증이 필수인 경우 추가
        new ExcludePath("/auth/logout", HttpMethod.POST),
        new ExcludePath("/pilgrimage/**", HttpMethod.POST),
        new ExcludePath("/posts/free/my-posts", HttpMethod.GET),
        new ExcludePath("/posts/free/my-comments", HttpMethod.GET),
        new ExcludePath("/pilgrimage/detail/**", HttpMethod.GET),
        new ExcludePath("/posts/free", HttpMethod.POST),
        new ExcludePath("/posts/free/**", HttpMethod.DELETE),
        new ExcludePath("/posts/free/**", HttpMethod.POST),
        new ExcludePath("/posts/free/**", HttpMethod.PUT),
        new ExcludePath("/posts/free/**", HttpMethod.PATCH),
        new ExcludePath("/posts/guestbooks/my-comments", HttpMethod.GET),
        new ExcludePath("/posts/guestbooks/my-posts", HttpMethod.GET),
        new ExcludePath("/my", HttpMethod.GET),
        new ExcludePath("/my/**", HttpMethod.GET),
        new ExcludePath("/posts/guestbooks/**", HttpMethod.PATCH),
        new ExcludePath("/posts/guestbooks/**", HttpMethod.DELETE),
        new ExcludePath("/posts/guestbooks/**", HttpMethod.POST),
        new ExcludePath("/my/blocks/**", HttpMethod.POST),
        new ExcludePath("/posts/free/comments/**", HttpMethod.PUT),
        new ExcludePath("/posts/free/comments/**", HttpMethod.DELETE),
        new ExcludePath("/posts/guestbooks/comments/**", HttpMethod.PUT),
        new ExcludePath("/posts/guestbooks/comments/**", HttpMethod.DELETE),
        new ExcludePath("/shop/purchase/**", HttpMethod.POST)
        // Add more paths and methods as needed
    );
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getServletPath();
        String method = request.getMethod();

        return !excludePaths.stream()
            .anyMatch(excludePath -> excludePath.matches(requestURI, method));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        // 1. Request Header 에서 JWT 토큰 추출
        String token = resolveToken((HttpServletRequest) request);
        String requestURI = httpServletRequest.getRequestURI();

        // 2. validateToken 으로 토큰 유효성 검사
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext 에 저장
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
        } else {
            log.info("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
        }
        chain.doFilter(request, response);
    }

    // Request Header 에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public class ExcludePath {
        private final String pathPattern;
        private final HttpMethod method;

        public ExcludePath(String pathPattern, HttpMethod method) {
            this.pathPattern = pathPattern;
            this.method = method;
        }

        public boolean matches(String requestURI, String method) {
            // 정규 표현식을 사용하여 매치 여부 확인
            String regex = pathPattern.replaceAll("\\*\\*", ".*");
            return Pattern.matches(regex, requestURI) && this.method.matches(method);
        }
    }

    private enum HttpMethod {
        GET, POST, PUT, DELETE, PATCH;  // Add more methods as needed

        public boolean matches(String requestMethod) {
            return this.name().equalsIgnoreCase(requestMethod);
        }
    }
}