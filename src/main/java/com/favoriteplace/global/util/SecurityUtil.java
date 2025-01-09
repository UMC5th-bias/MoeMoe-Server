package com.favoriteplace.global.util;

import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.member.repository.MemberRepository;
import com.favoriteplace.global.security.provider.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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

        //TODO: 유저 없을 경우 예외처리
        return memberRepository.findByEmail(authentication.getName()).get();
    }


    public String resolveToken(HttpServletRequest request) {
        // TODO: getUserFromHeader와 로직이 조금 겹치는 듯, 추후 수정 필요
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean isTokenExists(HttpServletRequest request) {
        return getUserFromHeader(request) != null;
    }
}
