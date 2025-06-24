package com.favoriteplace.app.common.converter;

import com.favoriteplace.app.common.dto.CommonResponseDto;

public class CommonConverter {
    public static CommonResponseDto.PostResponseDto toPostResponseDto(Boolean success, String message) {
        return CommonResponseDto.PostResponseDto.builder()
                .success(success)
                .message(message)
                .build();
    }

    public static CommonResponseDto.RallyResponseDto toRallyResponseDto(
            Boolean success, Boolean isComplete, String message
    ) {
        return CommonResponseDto.RallyResponseDto.builder()
                .success(success)
                .isComplete(isComplete)
                .message(message)
                .build();
    }
}
