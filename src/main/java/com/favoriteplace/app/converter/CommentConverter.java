package com.favoriteplace.app.converter;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.community.Comment;
import com.favoriteplace.app.domain.community.GuestBook;
import com.favoriteplace.app.dto.UserInfoResponseDto;
import com.favoriteplace.app.dto.community.comment.CommentParentResponseDto;
import com.favoriteplace.app.dto.community.guestbook.GuestBookResponseDto;
import com.favoriteplace.app.dto.community.PostResponseDto;
import com.favoriteplace.app.dto.community.comment.CommentSubResponseDto;
import com.favoriteplace.global.util.DateTimeFormatUtils;

import java.util.Collections;
import java.util.List;

public class CommentConverter {

    public static CommentParentResponseDto toComment(Comment comment, Member member, List<Comment> subComments) {
        if (comment.isDeleted()) {
            return new CommentParentResponseDto(hideUserInfo(comment), null, "[삭제된 댓글입니다.]", null, null,
                    toSubComments(subComments, member));
        } else {
            return new CommentParentResponseDto(showUserInfo(comment), comment.getId(), comment.getContent(),
                    DateTimeFormatUtils.getPassDateTime(comment.getCreatedAt()), isCommentWriter(member, comment),
                    toSubComments(subComments, member));
        }
    }

    public static List<CommentSubResponseDto> toSubComments(List<Comment> subComments, Member member) {
        if (subComments.isEmpty()) {
            return Collections.emptyList();
        }
        return subComments.stream().map(comment -> {
            if (comment.isDeleted()) {
                return new CommentSubResponseDto(hideUserInfo(comment), null, "[삭제된 댓글입니다.]", null, null, null);
            } else {
                String nickname = comment.getReferenceComment() == null ? null
                        : comment.getReferenceComment().getMember().getNickname();

                return new CommentSubResponseDto(showUserInfo(comment), comment.getId(), comment.getContent(),
                        DateTimeFormatUtils.getPassDateTime(comment.getCreatedAt()), isCommentWriter(member, comment),
                        nickname);
            }
        }).toList();
    }

    public static GuestBookResponseDto.MyGuestBookComment toMyGuestBookComment(Comment comment) {
        GuestBook guestBook = comment.getGuestBook();
        return GuestBookResponseDto.MyGuestBookComment.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .passedTime(DateTimeFormatUtils.getPassDateTime(comment.getCreatedAt()))
                .myGuestBookInfo(GuestBookResponseDto.MyGuestBookInfo.builder()
                        .id(guestBook.getId())
                        .title(guestBook.getTitle())
                        .nickname(guestBook.getMember().getNickname())
                        .views(guestBook.getView())
                        .likes(guestBook.getLikeCount())
                        .comments(getNotDeletedComment(guestBook.getComments()))
                        .passedTime(DateTimeFormatUtils.getPassDateTime(guestBook.getCreatedAt()))
                        .build())
                .build();
    }

    public static PostResponseDto.MyComment toMyPostComment(Comment comment) {
        return PostResponseDto.MyComment.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .passedTime(DateTimeFormatUtils.getPassDateTime(comment.getCreatedAt()))
                .post(PostConverter.toMyPost(comment.getPost()))
                .build();
    }

    /**
     * 사용자(앱을 사용하는 유저)가 댓글 작성자가 맞는지 확인하는 함수
     */
    public static Boolean isCommentWriter(Member member, Comment comment) {
        if (member == null) {
            return false;
        }
        return member.getId().equals(comment.getMember().getId());
    }


    /**
     * 삭제된 댓글이면 유저 정보 감춤
     */
    public static UserInfoResponseDto hideUserInfo(Comment comment) {
        return UserInfoResponseDto.builder()
                .id(null)
                .nickname("[알 수 없음]")
                .profileImageUrl(null)
                .profileTitleUrl(null)
                .profileIconUrl(null)
                .build();
    }

    public static UserInfoResponseDto showUserInfo(Comment comment) {
        Member member = comment.getMember();
        return UserInfoResponseDto.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImageUrl())
                .profileTitleUrl(
                        member.getProfileTitle() != null ? member.getProfileTitle().getDefaultImage().getUrl() : null)
                .profileIconUrl(
                        member.getProfileIcon() != null ? member.getProfileIcon().getDefaultImage().getUrl() : null)
                .build();
    }

    private static long getNotDeletedComment(List<Comment> comments) {
        return comments.stream()
                .filter(comment -> !comment.isDeleted())
                .count();
    }
}
