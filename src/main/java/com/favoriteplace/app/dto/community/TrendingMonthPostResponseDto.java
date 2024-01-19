package com.favoriteplace.app.dto.community;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TrendingMonthPostResponseDto {
    private Long id;
    private String title;
    private String type;
}