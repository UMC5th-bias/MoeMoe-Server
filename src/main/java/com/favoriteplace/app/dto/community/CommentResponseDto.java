package com.favoriteplace.app.dto.community;

import com.favoriteplace.app.dto.UserInfoResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class CommentResponseDto {
    @Getter
    @Builder
    public static class PostCommentDto {
        private Long page;
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
}
