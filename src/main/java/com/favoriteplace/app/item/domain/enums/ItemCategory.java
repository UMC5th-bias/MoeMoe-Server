package com.favoriteplace.app.item.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ItemCategory {
    NEW(0, "New!"),
    UMC(1, "UMC"),
    NORMAL(2, "Normal"),
    ;

    private final Integer num;
    private final String name;

}
