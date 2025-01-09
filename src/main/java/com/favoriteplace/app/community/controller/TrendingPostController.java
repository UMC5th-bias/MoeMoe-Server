package com.favoriteplace.app.community.controller;

import com.favoriteplace.app.community.controller.dto.TrendingPostResponseDto;
import com.favoriteplace.app.community.service.GuestBookQueryService;
import com.favoriteplace.app.community.service.PostQueryService;
import com.favoriteplace.app.service.TotalPostService;
import com.favoriteplace.global.util.DateTimeFormatUtils;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/posts/trending")
@RequiredArgsConstructor
public class TrendingPostController {
    private final PostQueryService postService;
    private final GuestBookQueryService guestBookQueryService;
    private final TotalPostService totalPostService;

    @GetMapping("/today/free")
    public TrendingPostResponseDto.TrendingTodayPostResponseDto getTodayTrendingFreePost() {
        return TrendingPostResponseDto.TrendingTodayPostResponseDto.builder()
                .date(DateTimeFormatUtils.convertDateToString(LocalDateTime.now()))
                .rank(postService.getTodayTrendingPost())
                .build();
    }

    @GetMapping("/today/guestbooks")
    public TrendingPostResponseDto.TrendingTodayPostResponseDto getTodayTrendingGuestBook() {
        return TrendingPostResponseDto.TrendingTodayPostResponseDto.builder()
                .date(DateTimeFormatUtils.convertDateToString(LocalDateTime.now()))
                .rank(guestBookQueryService.getTodayTrendingGuestBook())
                .build();
    }

    @GetMapping("/month")
    public List<TrendingPostResponseDto.TrendingMonthPostResponseDto> getMonthTrendingPost() {
        return totalPostService.getMonthTrendingPosts();
    }

}
