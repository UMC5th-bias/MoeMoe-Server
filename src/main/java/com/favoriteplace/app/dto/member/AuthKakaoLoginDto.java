package com.favoriteplace.app.dto.member;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AuthKakaoLoginDto(
        @JsonProperty("kakao_account")
        KakaoAccount kakaoAccount

) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record KakaoAccount(
            String email
    ) {

    }
}
