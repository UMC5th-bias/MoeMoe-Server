package com.favoriteplace.app.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PointType {
    ACQUIRE(0, "포인트 획득"),
    SPEND(1, "포인트 소비");

    private final Integer num;
    private final String description;
}
