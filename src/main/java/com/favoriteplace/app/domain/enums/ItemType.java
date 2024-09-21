package com.favoriteplace.app.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ItemType {
    ICON(0, "아이콘"),
    TITLE(1, "칭호");

    private final Integer num;
    private final String description;
}
