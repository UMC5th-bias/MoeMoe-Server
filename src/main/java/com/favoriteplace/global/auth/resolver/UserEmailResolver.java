package com.favoriteplace.global.auth.resolver;

import com.favoriteplace.global.auth.provider.JwtTokenProvider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;


@Component
public class UserEmailResolver implements HandlerMethodArgumentResolver {

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
        return SecurityContextHolder.getContext()
                .getAuthentication().getName();
    }

}