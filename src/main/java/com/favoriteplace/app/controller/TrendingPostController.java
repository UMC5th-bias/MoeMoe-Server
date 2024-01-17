package com.favoriteplace.app.controller;

import com.favoriteplace.app.dto.community.TrendingFreePostResponseDto;
import com.favoriteplace.app.service.PostService;
import com.favoriteplace.global.util.DateTimeFormatUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/posts/trending")
@RequiredArgsConstructor
public class TrendingPostController {
    private final PostService postService;

    @GetMapping("/today/free")
    public TrendingFreePostResponseDto getTodayTrendingFreePost(){
        //당일 기준 으로 TOP 5 뽑아서 추천수 기준으로 내림차순 정렬
        return TrendingFreePostResponseDto.builder()
                .date(DateTimeFormatUtils.convertDateToString(LocalDateTime.now()))
                .rank(postService.getTodayTrendingPost())
                .build();
    }
}
