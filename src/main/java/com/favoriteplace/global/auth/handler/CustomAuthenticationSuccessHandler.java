package com.favoriteplace.global.auth.handler;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.dto.member.TokenInfoDto;
import com.favoriteplace.app.repository.MemberRepository;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.auth.provider.JwtProvider;
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

    private final JwtProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    /**
     * User
     *
     */
    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {

        //유저 정보 가져오기
        String email = request.getParameter("email");
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_FOUND));

        //인증 정보를 기반으로 JWT 토큰 생성
        response.setContentType(APPLICATION_JSON_VALUE);
        TokenInfoDto tokenInfo = jwtTokenProvider.generateToken(member.getEmail());

        //refreshToken 업데이트
        member.updateRefreshToken(tokenInfo.refreshToken());

        new ObjectMapper().writeValue(response.getWriter(), tokenInfo);
    }
}
