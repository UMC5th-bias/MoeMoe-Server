package com.favoriteplace.app.dto.community;

import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.dto.UserInfoResponseDto;
import com.favoriteplace.global.util.DateTimeFormatUtils;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostResponseDto {

    @Getter
    @Builder
    public static class PostDetailResponseDto{
        private UserInfoResponseDto userInfo;
        private PostInfoResponseDto postInfo;
    }

    @Getter
    @Builder
    public static class PostInfoResponseDto{
        private Long id;
        private String title;
        private String content;
        private Long view;
        private Long likes;
        private Boolean isLike;
        private Boolean isWrite;
        private String createdAt;
        private List<String> image;

        public static PostInfoResponseDto of(Post post, Boolean isLike, Boolean isWrite, List<String> images){
            return PostInfoResponseDto.builder()
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