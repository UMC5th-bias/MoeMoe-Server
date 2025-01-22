package com.favoriteplace.app.rally.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RallyVersion {
    v1("BETA 1.0"),
    ;
    private final String version;
}
