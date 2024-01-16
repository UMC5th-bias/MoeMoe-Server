package com.favoriteplace.app.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberDto {

    @Getter
    @NoArgsConstructor
    public static class MemberLoginRequestDto {
        private String email;
        private String password;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class TokenInfo {

        private String grantType;
        private String accessToken;
        private String refreshToken;
    }

}
