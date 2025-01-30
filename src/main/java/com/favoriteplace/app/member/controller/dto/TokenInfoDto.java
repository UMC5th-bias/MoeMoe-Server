package com.favoriteplace.app.member.controller.dto;


public record TokenInfoDto(
        String grantType,
        String accessToken,
        String refreshToken
) {
    public static TokenInfoDto of(
            final String accessToken,
            final String refreshToken
    ) {
        return new TokenInfoDto("Bearer", accessToken, refreshToken);
    }
}
