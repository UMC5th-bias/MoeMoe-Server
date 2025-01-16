package com.favoriteplace.global.auth.config;

import com.favoriteplace.global.auth.filter.ExceptionHandlerFilter;
import com.favoriteplace.global.auth.filter.JwtAuthenticationFilter;
import com.favoriteplace.global.auth.filter.LoginFilter;
import com.favoriteplace.global.auth.handler.CustomAuthenticationFailHandler;
import com.favoriteplace.global.auth.handler.CustomAuthenticationSuccessHandler;
import com.favoriteplace.global.auth.provider.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate redisTemplate;
    private final ExceptionHandlerFilter exceptionHandlerFilter;
    private final AuthenticationManagerBuilder authManagerBuilder;
    private final CustomAuthenticationSuccessHandler successHandler;
    private final CustomAuthenticationFailHandler failureHandler;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        LoginFilter loginFilter = new LoginFilter(authManagerBuilder.getOrBuild());
        loginFilter.setFilterProcessesUrl("/auth/login");

        loginFilter.setAuthenticationFailureHandler(failureHandler);
        loginFilter.setAuthenticationSuccessHandler(successHandler);

        http.httpBasic(HttpBasicConfigurer::disable)
            .csrf(CsrfConfigurer::disable)
            .cors(Customizer.withDefaults())
            .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilter(loginFilter)
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, redisTemplate), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(exceptionHandlerFilter, JwtAuthenticationFilter.class)
            ;

        return http.build();
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
