package com.favoriteplace.global.security.Filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        // 사용자가 제공한 인증 정보 추출
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // UsernamePasswordAuthenticationToken 생성
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
            email, password);

        // 실제 인증을 수행하도록 AuthenticationManager에게 위임
        return authenticationManager.authenticate(authRequest);

    }

}
