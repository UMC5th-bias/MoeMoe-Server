package com.favoriteplace.app.service.fcm.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenMessage {
    POST_NEW_COMMENT("post", "내 글에 새로운 댓글이 달렸어요!", "null")
    ;

    private final String type;
    private final String title;
    private final String message;
}
