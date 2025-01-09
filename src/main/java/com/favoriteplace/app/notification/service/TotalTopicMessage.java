package com.favoriteplace.app.notification.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TotalTopicMessage {
    INFORM("home", "새로운 기능이 업데이트 되었어요!", "새로운 기능을 확인해보세요!")
    ;

    private final String type;
    private final String title;
    private final String message;
}
