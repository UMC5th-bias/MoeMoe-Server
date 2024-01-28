package com.favoriteplace.app.dto.community;

import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.dto.UserInfoResponseDto;
import com.favoriteplace.global.gcpImage.ConvertUuidToUrl;
import com.favoriteplace.global.util.DateTimeFormatUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.N;

import java.util.ArrayList;
import java.util.List;

public class PostResponseDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SuccessResponseDto{
        private String message;
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
        private Boolean isLike;
        private Boolean isWrite;
        private String passedTime;
        private List<String> image;

        public static PostInfo of(Post post, Boolean isLike, Boolean isWrite, List<String> images){
            List<String> convertedImages;
            if (images.isEmpty()) {
                convertedImages = new ArrayList<>(); // 빈 리스트일 경우, 그대로 빈 리스트를 반환
            } else {
                convertedImages = images.stream().map(ConvertUuidToUrl::convertUuidToUrl).toList();
            }
            return PostInfo.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .view(post.getView())
                    .likes(post.getLikeCount())
                    .isLike(isLike)
                    .isWrite(isWrite)
                    .passedTime(DateTimeFormatUtils.getPassDateTime(post.getCreatedAt()))
                    .image(convertedImages)
                    .build();
        }
    }
}