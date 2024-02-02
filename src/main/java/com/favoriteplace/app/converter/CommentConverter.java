package com.favoriteplace.app.converter;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.community.Comment;
import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.dto.UserInfoResponseDto;
import com.favoriteplace.app.dto.community.CommentResponseDto;
import com.favoriteplace.app.dto.community.GuestBookResponseDto;
import com.favoriteplace.app.dto.community.PostResponseDto;
import com.favoriteplace.global.util.DateTimeFormatUtils;

public class CommentConverter {

    public static GuestBookResponseDto.MyGuestBookComment toMyGuestBookComment(Comment comment, Long comments) {
        return GuestBookResponseDto.MyGuestBookComment.builder()
                .id(comment.getId()).content(comment.getContent()).passedTime(DateTimeFormatUtils.getPassDateTime(comment.getCreatedAt()))
                .guestBook(GuestBookResponseDto.GuestBook.builder()
                        .id(comment.getGuestBook().getId())
                        .title(comment.getGuestBook().getTitle())
                        .nickname(comment.getGuestBook().getMember().getNickname())
                        .views(comment.getGuestBook().getView())
                        .likes(comment.getGuestBook().getLikeCount())
                        .comments(comments)
                        .build())
                .build();
    }

    public static CommentResponseDto.PostComment toPostComment(Comment comment, Boolean isWrite){
        return CommentResponseDto.PostComment.builder()
                .userInfo(UserInfoResponseDto.of(comment.getMember()))
                .id(comment.getId())
                .content(comment.getContent())
                .passedTime(DateTimeFormatUtils.getPassDateTime(comment.getCreatedAt()))
                .isWrite(isWrite)
                .build();
    }

    public static PostResponseDto.MyComment toMyGuestBookComment(Comment comment, Member member, Post post, Long commentCount){
        return PostResponseDto.MyComment.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .passedTime(DateTimeFormatUtils.getPassDateTime(comment.getCreatedAt()))
                .post(PostConverter.toMyPost(post, member, commentCount))
                .build();
    }
}
