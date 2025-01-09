package com.favoriteplace.app.converter;

import com.favoriteplace.app.domain.Image;
import com.favoriteplace.app.domain.community.Comment;
import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.member.controller.dto.UserInfoResponseDto;
import com.favoriteplace.app.dto.community.PostResponseDto;
import com.favoriteplace.global.util.DateTimeFormatUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PostConverter {
    public static PostResponseDto.MyPost toMyPost(Post post) {
        return PostResponseDto.MyPost.builder()
                .id(post.getId())
                .title(post.getTitle())
                .nickname(post.getMember().getNickname())
                .views(post.getView())
                .likes(post.getLikeCount())
                .comments(getNotDeletedComment(post.getComments()))
                .passedTime(DateTimeFormatUtils.getPassDateTime(post.getCreatedAt())).build();
    }

    public static PostResponseDto.PostDetailResponseDto toPostDetailResponse(
            Post post, boolean isLike, boolean isWriter) {
        return PostResponseDto.PostDetailResponseDto.builder()
                .userInfo(UserInfoResponseDto.of(post.getMember()))
                .postInfo(toPostInfo(post, isLike, isWriter))
                .build();
    }

    public static PostResponseDto.PostInfo toPostInfo(Post post, Boolean isLike, Boolean isWrite) {
        List<String> images = Optional.ofNullable(post.getImages())
                .map(image -> image.stream().map(Image::getUrl).collect(Collectors.toList()))
                .orElse(Collections.emptyList());
        return PostResponseDto.PostInfo.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .view(post.getView())
                .likes(post.getLikeCount())
                .comments(getNotDeletedComment(post.getComments()))
                .isLike(isLike)
                .isWrite(isWrite)
                .passedTime(DateTimeFormatUtils.getPassDateTime(post.getCreatedAt()))
                .image(images)
                .build();
    }

    private static long getNotDeletedComment(List<Comment> comments) {
        return comments.stream()
                .filter(comment -> !comment.isDeleted())
                .count();
    }
}
