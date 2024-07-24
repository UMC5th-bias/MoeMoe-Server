package com.favoriteplace.app.dto.community;

import com.favoriteplace.app.dto.UserInfoResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class CommentResponseDto {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentDto {
        private Long page;
        private Long size;
        private List<ParentComment> parentComment;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParentComment {
        private UserInfoResponseDto userInfo;
        private Long id;
        private String content;
        private String passedTime;
        private Boolean isWrite;
        private List<SubComment> subComments;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubComment {
        private UserInfoResponseDto userInfo;
        private Long id;
        private String content;
        private String passedTime;
        private Boolean isWrite;
        private String referenceNickname;
    }

}
