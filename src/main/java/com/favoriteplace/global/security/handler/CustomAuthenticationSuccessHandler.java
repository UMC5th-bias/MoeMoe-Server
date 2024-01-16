package com.favoriteplace.global.security.handler;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.favoriteplace.app.dto.member.MemberDto.TokenInfo;
import com.favoriteplace.global.security.provider.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {

        //인증 정보를 기반으로 JWT 토큰 생성
        response.setContentType(APPLICATION_JSON_VALUE);
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        new ObjectMapper().writeValue(response.getWriter(), tokenInfo);
    }
}
