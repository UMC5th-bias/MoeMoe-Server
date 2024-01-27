package com.favoriteplace.app.dto.community;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GuestBookRequestDto {
    private String title;
    private String content;
    private List<String> hashtags;
}
