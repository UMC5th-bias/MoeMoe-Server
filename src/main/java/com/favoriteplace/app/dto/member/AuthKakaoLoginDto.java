package com.favoriteplace.app.dto.member;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthKakaoLoginDto(
        @JsonProperty("kakao_account")
        KakaoAccount kakaoAccount

) {
    public record KakaoAccount(
            String email
    ) {

    }
}
