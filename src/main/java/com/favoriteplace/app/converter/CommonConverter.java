package com.favoriteplace.app.converter;

import com.favoriteplace.app.dto.CommonResponseDto;

public class CommonConverter {
    public static CommonResponseDto.PostResponseDto toPostResponseDto(Boolean success, String message){
        return CommonResponseDto.PostResponseDto.builder()
                .success(success)
                .message(message)
                .build();
    }
}
