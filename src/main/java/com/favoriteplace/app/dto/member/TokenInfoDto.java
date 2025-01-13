package com.favoriteplace.app.dto.member;

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
