package com.favoriteplace.global.auth;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class JwtAuthenticationNeededPath {
    private final String pathPattern;
    private final String method;

    public boolean matches(String requestURI, String method) {
        String regex = pathPattern.replaceAll("\\*\\*", ".*");
        return Pattern.matches(regex, requestURI) && this.method.equalsIgnoreCase(method);
    }

    public static final List<JwtAuthenticationNeededPath> NEEDED_JWT_AUTHENTICATION_PATHS = Arrays.asList(
            new JwtAuthenticationNeededPath("/auth/logout", "POST"),
            new JwtAuthenticationNeededPath("/pilgrimage/**", "POST"),
            new JwtAuthenticationNeededPath("/pilgrimage/**", "DELETE"),
            new JwtAuthenticationNeededPath("/posts/free/my-posts", "GET"),
            new JwtAuthenticationNeededPath("/posts/free/my-comments", "GET"),
            new JwtAuthenticationNeededPath("/posts/free", "POST"),
            new JwtAuthenticationNeededPath("/posts/free/**", "DELETE"),
            new JwtAuthenticationNeededPath("/posts/free/**", "POST"),
            new JwtAuthenticationNeededPath("/posts/free/**", "PUT"),
            new JwtAuthenticationNeededPath("/posts/free/**", "PATCH"),
            new JwtAuthenticationNeededPath("/posts/guestbooks/my-comments", "GET"),
            new JwtAuthenticationNeededPath("/posts/guestbooks/my-posts", "GET"),
            new JwtAuthenticationNeededPath("/my", "GET"),
            new JwtAuthenticationNeededPath("/my/**", "GET"),
            new JwtAuthenticationNeededPath("/my/**", "PUT"),
            new JwtAuthenticationNeededPath("/my/**", "PATCH"),
            new JwtAuthenticationNeededPath("/posts/guestbooks/**", "PATCH"),
            new JwtAuthenticationNeededPath("/posts/guestbooks/**", "DELETE"),
            new JwtAuthenticationNeededPath("/posts/guestbooks/**", "POST"),
            new JwtAuthenticationNeededPath("/my/blocks/**", "POST"),
            new JwtAuthenticationNeededPath("/posts/free/comments/**", "PUT"),
            new JwtAuthenticationNeededPath("/posts/free/comments/**", "DELETE"),
            new JwtAuthenticationNeededPath("/posts/guestbooks/comments/**", "PUT"),
            new JwtAuthenticationNeededPath("/posts/guestbooks/comments/**", "DELETE"),
            new JwtAuthenticationNeededPath("/shop/purchase/**", "POST"),
            new JwtAuthenticationNeededPath("/notifications", "PATCH"),
            new JwtAuthenticationNeededPath("/notifications", "GET"),
            new JwtAuthenticationNeededPath("/notifications/**", "PATCH"),
            new JwtAuthenticationNeededPath("/notifications/**", "DELETE")
    );

    public static List<JwtAuthenticationNeededPath> getNeededPaths() {
        return NEEDED_JWT_AUTHENTICATION_PATHS;
    }
}
