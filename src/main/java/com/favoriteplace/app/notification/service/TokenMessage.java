package com.favoriteplace.app.notification.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenMessage {
    POST_NEW_COMMENT("post", "내 글에 새로운 댓글이 달렸어요!"),
    GUESTBOOK_NEW_COMMENT("guestBook", "내 인증글에 새로운 댓글이 달렸어요!"),
    POST_COMMENT_NEW_SUBCOMMENT("post", "내 댓글에 새로운 답글이 달렸어요!"),
    GUESTBOOK_COMMENT_NEW_SUBCOMMENT("guestBook", "내 댓글에 새로운 답글이 달렸어요!")
    ;

    private final String type;
    private final String title;
}
