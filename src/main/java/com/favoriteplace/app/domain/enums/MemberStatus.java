package com.favoriteplace.app.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberStatus {
    Y(0, "활성화"),
    N(1, "비활성화"),
    ;

    private final Integer value;
    private final String status;
}
