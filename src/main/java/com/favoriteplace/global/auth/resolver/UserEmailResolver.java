package com.favoriteplace.global.auth.resolver;

import com.favoriteplace.global.auth.provider.JwtTokenProvider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;


@Component
@Slf4j
@RequiredArgsConstructor
public class UserEmailResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        //UserEmail annotaion을 붙였는지, parameter 타입이 String 타입인지 확인
        return parameter.hasParameterAnnotation(UserEmail.class) && String.class.equals(parameter.getParameterType());
    }

    @Override
    public String resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer modelAndViewContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        if(userEmail.equals("anonymousUser")) {
            String bearerToken = request.getHeader("Authorization");
            if(bearerToken == null) {
                return null;
            }
            String token = bearerToken.substring(7);
            jwtTokenProvider.validateToken(token);
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            return authentication.getName();
        }
        return userEmail;
    }

}