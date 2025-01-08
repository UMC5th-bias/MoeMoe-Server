package com.favoriteplace.global.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        setResponse(response);
    }


    private void setResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter()
                .write(objectMapper.writeValueAsString(
                        ErrorResponse.of(ErrorCode.JWT_UNAUTHORIZED_EXCEPTION.getHttpStatus(),
                                ErrorCode.JWT_UNAUTHORIZED_EXCEPTION.getCode(),
                                ErrorCode.JWT_UNAUTHORIZED_EXCEPTION.getMessage())));
    }

}
