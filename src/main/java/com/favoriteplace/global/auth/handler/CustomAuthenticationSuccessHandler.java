package com.favoriteplace.global.auth.handler;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.favoriteplace.app.member.controller.dto.TokenInfoDto;
import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.member.repository.MemberRepository;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.auth.provider.JwtTokenProvider;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        String email = request.getParameter("email");
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_FOUND));

        String issuedRefreshToken = jwtTokenProvider.issueRefreshToken(member.getEmail());
        String issuedAccessToken = jwtTokenProvider.issueRefreshToken(member.getEmail());

        TokenInfoDto tokenInfo = TokenInfoDto.of(issuedAccessToken, issuedRefreshToken);
        member.updateRefreshToken(tokenInfo.refreshToken());

        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getWriter(), tokenInfo);
    }
}
