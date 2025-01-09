package com.favoriteplace.app.converter;

import com.favoriteplace.app.domain.community.Comment;
import com.favoriteplace.app.domain.community.GuestBook;
import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.notification.controller.dto.PostTokenCond;
import com.favoriteplace.app.notification.service.TokenMessage;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;

public class FcmConverter {

    /**
     * 게시글 작성자에게 알림 전송
     */
    public static PostTokenCond toPostWriter(Post post, Comment newComment) {
        if (post.getMember().getFcmToken() == null) {
            throw new RestApiException(ErrorCode.FCM_TOKEN_NOT_FOUND);
        }
        return PostTokenCond.builder()
                .token(post.getMember().getFcmToken())
                .tokenMessage(TokenMessage.POST_NEW_COMMENT)
                .postId(post.getId())
                .message(newComment.getContent())
                .build();
    }

    /**
     * 성지순례 인증글 작성자에게 알림 전송
     */
    public static PostTokenCond toGuestBookWriter(GuestBook guestBook, Comment newComment) {
        if (guestBook.getMember().getFcmToken() == null) {
            throw new RestApiException(ErrorCode.FCM_TOKEN_NOT_FOUND);
        }
        return PostTokenCond.builder()
                .token(guestBook.getMember().getFcmToken())
                .tokenMessage(TokenMessage.GUESTBOOK_NEW_COMMENT)
                .guestBookId(guestBook.getId())
                .message(newComment.getContent())
                .build();
    }

    public static PostTokenCond toParentCommentWriter(Post post, Comment newComment) {
        if (newComment.getParentComment().getMember().getFcmToken() == null) {
            throw new RestApiException(ErrorCode.FCM_TOKEN_NOT_FOUND);
        }
        return PostTokenCond.builder()
                .token(newComment.getParentComment().getMember().getFcmToken())
                .tokenMessage(TokenMessage.POST_COMMENT_NEW_SUBCOMMENT)
                .postId(post.getId())
                .message(newComment.getContent())
                .build();
    }

    public static PostTokenCond toReferCommentWriter(Post post, Comment newComment) {
        if (newComment.getReferenceComment().getMember().getFcmToken() == null) {
            throw new RestApiException(ErrorCode.FCM_TOKEN_NOT_FOUND);
        }
        return PostTokenCond.builder()
                .token(newComment.getReferenceComment().getMember().getFcmToken())
                .tokenMessage(TokenMessage.POST_COMMENT_NEW_SUBCOMMENT)
                .postId(post.getId())
                .message(newComment.getContent())
                .build();
    }

    public static PostTokenCond toParentCommentWriter(GuestBook guestBook, Comment newComment) {
        if (newComment.getParentComment().getMember().getFcmToken() == null) {
            throw new RestApiException(ErrorCode.FCM_TOKEN_NOT_FOUND);
        }
        return PostTokenCond.builder()
                .token(newComment.getParentComment().getMember().getFcmToken())
                .tokenMessage(TokenMessage.GUESTBOOK_COMMENT_NEW_SUBCOMMENT)
                .guestBookId(guestBook.getId())
                .message(newComment.getContent())
                .build();
    }

    public static PostTokenCond toReferCommentWriter(GuestBook guestBook, Comment newComment) {
        if (newComment.getReferenceComment().getMember().getFcmToken() == null) {
            throw new RestApiException(ErrorCode.FCM_TOKEN_NOT_FOUND);
        }
        return PostTokenCond.builder()
                .token(newComment.getReferenceComment().getMember().getFcmToken())
                .tokenMessage(TokenMessage.GUESTBOOK_COMMENT_NEW_SUBCOMMENT)
                .guestBookId(guestBook.getId())
                .message(newComment.getContent())
                .build();
    }


}
