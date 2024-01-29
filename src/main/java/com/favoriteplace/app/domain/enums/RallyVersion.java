package com.favoriteplace.app.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public enum RallyVersion {
    v1("BETA 1.0");
    private final String version;
}
