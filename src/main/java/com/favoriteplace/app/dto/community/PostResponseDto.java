package com.favoriteplace.app.dto.community;

import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.dto.UserInfoResponseDto;
import com.favoriteplace.global.util.DateTimeFormatUtils;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PostResponseDto {

    @Builder
    @Getter
    public static class SuccessResponseDto{
        private String message;
    }

    @Builder
    @Getter
    public static class MyCommentDto{
        private Long size;
        private List<MyComment>comment;
    }

    @Getter
    @Builder
    public static class MyComment{
        private Long id;
        private String content;
        private String passedTime;
        private MyPost post;
    }

    @Getter
    @Builder
    public static class MyPostResponseDto{
        private Long size;
        private List<MyPost> post;
    }

    @Getter
    @Builder
    public static class MyPost{
        private Long id;
        private String title;
        private String nickname;
        private Long views;
        private Long likes;
        private Long comments;
        private String passedTime;
    }

    @Getter
    @Builder
    public static class PostCommentResponseDto{
        private Long size;
        private List<PostComment> comment;
    }

    @Getter
    @Builder
    public static class PostComment{
        private UserInfoResponseDto userInfo;
        private Long id;
        private String content;
        private String passedTime;
        private Boolean isWrite;
    }

    @Getter
    @Builder
    public static class PostDetailResponseDto{
        private UserInfoResponseDto userInfo;
        private PostInfo postInfo;
    }

    @Getter
    @Builder
    public static class PostInfo {
        private Long id;
        private String title;
        private String content;
        private Long view;
        private Long likes;
        private Boolean isLike;
        private Boolean isWrite;
        private String createdAt;
        private List<String> image;

        public static PostInfo of(Post post, Boolean isLike, Boolean isWrite, List<String> images){
            return PostInfo.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .view(post.getView())
                    .likes(post.getLikeCount())
                    .isLike(isLike)
                    .isWrite(isWrite)
                    .createdAt(DateTimeFormatUtils.convertDateToString(post.getCreatedAt()))
                    .image(images)
                    .build();
        }
    }





}