package com.favoriteplace.app.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CommonResponseDto {
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostResponseDto{
        Boolean success;
        String message;
    }
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RallyResponseDto {
        Boolean success;
        String message;
        Boolean isComplete;
    }
}
