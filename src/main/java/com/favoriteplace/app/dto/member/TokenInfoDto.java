package com.favoriteplace.app.dto.member;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record TokenInfoDto(
        String grantType,
        String accessToken,
        String refreshToken
) {
    public static TokenInfoDto of(final String accessToken, final String refreshToken) {
        return TokenInfoDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
