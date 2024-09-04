package com.favoriteplace.app.converter;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.Notification;
import com.favoriteplace.app.domain.community.Comment;
import com.favoriteplace.app.domain.community.GuestBook;
import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.dto.NotificationResponseDto;
import com.favoriteplace.app.dto.NotificationResponseDto.NotificationInfo;
import com.favoriteplace.app.service.fcm.enums.TokenMessage;
import com.favoriteplace.global.util.DateTimeFormatUtils;
import org.springframework.data.domain.Page;

public class NotificationConverter {
    public static NotificationResponseDto toNotificationResponseDto(Page<Notification> notifications){
        return NotificationResponseDto.builder()
                .page(notifications.getNumber())
                .size(notifications.getSize())
                .notifications(
                        notifications.getContent().stream()
                                .map(n -> NotificationInfo.builder()
                                        .id(n.getId())
                                        .type(n.getType())
                                        .date(DateTimeFormatUtils.getPassDateTime(n.getCreatedAt()))
                                        .title(n.getTitle())
                                        .content(n.getContent())
                                        .postId(n.getPostId())
                                        .guestBookId(n.getGuestBookId())
                                        .rallyId(n.getRallyId())
                                        .isRead(n.getIsRead())
                                        .build())
                                .toList()
                )
                .build();
    }

    public static Notification toPostNewComment(Post post, Comment comment){
        return Notification.builder()
                .type(TokenMessage.POST_NEW_COMMENT.getType())
                .title(TokenMessage.POST_NEW_COMMENT.getTitle())
                .content(comment.getContent())
                .postId(post.getId())
                .member(post.getMember())
                .build();
    }

    public static Notification toPostParentNewSubComment(Post post, Comment comment){
        return Notification.builder()
                .type(TokenMessage.POST_COMMENT_NEW_SUBCOMMENT.getType())
                .title(TokenMessage.POST_COMMENT_NEW_SUBCOMMENT.getTitle())
                .content(comment.getContent())
                .postId(post.getId())
                .member(comment.getParentComment().getMember())
                .build();
    }

    public static Notification toPostReferNewSubComment(Post post, Comment comment){
        return Notification.builder()
                .type(TokenMessage.POST_COMMENT_NEW_SUBCOMMENT.getType())
                .title(TokenMessage.POST_COMMENT_NEW_SUBCOMMENT.getTitle())
                .content(comment.getContent())
                .postId(post.getId())
                .member(comment.getReferenceComment().getMember())
                .build();
    }

    public static Notification toGuestBookNewComment(GuestBook guestBook, Comment comment){
        return Notification.builder()
                .type(TokenMessage.GUESTBOOK_NEW_COMMENT.getType())
                .title(TokenMessage.GUESTBOOK_NEW_COMMENT.getTitle())
                .content(comment.getContent())
                .guestBookId(guestBook.getId())
                .member(guestBook.getMember())
                .build();
    }

    public static Notification toGuestBookParentNewSubComment(GuestBook guestBook, Comment comment){
        return Notification.builder()
                .type(TokenMessage.GUESTBOOK_COMMENT_NEW_SUBCOMMENT.getType())
                .title(TokenMessage.GUESTBOOK_COMMENT_NEW_SUBCOMMENT.getTitle())
                .content(comment.getContent())
                .guestBookId(guestBook.getId())
                .member(comment.getParentComment().getMember())
                .build();
    }

    public static Notification toGuestBookReferNewSubComment(GuestBook guestBook, Comment comment){
        return Notification.builder()
                .type(TokenMessage.GUESTBOOK_COMMENT_NEW_SUBCOMMENT.getType())
                .title(TokenMessage.GUESTBOOK_COMMENT_NEW_SUBCOMMENT.getTitle())
                .content(comment.getContent())
                .guestBookId(guestBook.getId())
                .member(comment.getReferenceComment().getMember())
                .build();
    }

}
