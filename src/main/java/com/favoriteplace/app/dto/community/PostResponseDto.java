package com.favoriteplace.app.dto.community;

import com.favoriteplace.app.member.controller.dto.UserInfoResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class PostResponseDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentSuccessResponseDto {
        private Long commentId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikeSuccessResponseDto{
        private Long likedId;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostIdResponseDto{
        private Long postId;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GuestBookIdResponseDto{
        private Long guestBookId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyCommentDto{
        private Long page;
        private Long size;
        private List<MyComment>comment;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyComment{
        private Long id;
        private String content;
        private String passedTime;
        private MyPost post;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyPostResponseDto{
        private Long page;
        private Long size;
        private List<MyPost> post;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
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
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostDetailResponseDto{
        private UserInfoResponseDto userInfo;
        private PostInfo postInfo;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostInfo {
        private Long id;
        private String title;
        private String content;
        private Long view;
        private Long likes;
        private Long comments;
        private Boolean isLike;
        private Boolean isWrite;
        private String passedTime;
        private List<String> image;
    }
}