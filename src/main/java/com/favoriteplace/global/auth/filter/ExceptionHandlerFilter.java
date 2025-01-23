package com.favoriteplace.global.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.ErrorResponse;
import com.favoriteplace.global.exception.RestApiException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (RestApiException e) {
            setErrorResponse(response, e.getErrorCode());
            log.warn("{}, uri: {}", e.getErrorCode().getMessage(), request.getRequestURI());
        }
    }

    private void setErrorResponse(
            HttpServletResponse response,
            ErrorCode errorCode
    ) {
        ObjectMapper objectMapper = new ObjectMapper();

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = ErrorResponse.of(
                errorCode.getHttpStatus(), errorCode.getCode(), errorCode.getMessage()
        );

        try {
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
