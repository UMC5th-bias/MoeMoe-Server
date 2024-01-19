package com.favoriteplace.global.util;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.repository.MemberRepository;

import com.favoriteplace.global.security.provider.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
@Component
public class SecurityUtil {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    public static final String TOKEN_HEADER_PREFIX = "Bearer ";

    public Member getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return memberRepository.findByEmail(authentication.getName()).get();
    }

    public Member getUserFromHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_HEADER_PREFIX)) {
            return null;
        }
        String token = resolveToken(request);

        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        return memberRepository.findByEmail(authentication.getName()).get();
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
