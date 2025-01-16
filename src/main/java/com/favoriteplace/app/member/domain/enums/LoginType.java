package com.favoriteplace.app.member.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoginType {
    SELF(0, "자체"),
    GOOGLE(1, "구글"),
    KAKAO(2, "카카오"),
    NAVER(3, "네이버"),
    ;

    private final Integer value;
    private final String type;
}
