package com.favoriteplace.app.controller;

import com.favoriteplace.app.dto.HomeResponseDto;
import com.favoriteplace.app.service.MemberService;
import com.favoriteplace.app.service.RallyService;
import com.favoriteplace.app.service.TotalPostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {
    private final MemberService memberService;
    private final RallyService rallyService;
    private final TotalPostService totalPostService;

    @GetMapping()
    public HomeResponseDto getHomeInfo(HttpServletRequest request) {

        if (memberService.isTokenExists(request)) {
            return HomeResponseDto.builder()
                    .isLoggedIn(true)
                    .userInfo(memberService.getUserInfo(request))
                    .rally(rallyService.getMemberRecentRally(request))
                    .trendingPosts(totalPostService.getTrendingPosts())
                    .build();
        } else {
            return HomeResponseDto.builder()
                    .isLoggedIn(false)
                    .userInfo(null)
                    .rally(rallyService.getRandomRally())
                    .trendingPosts(totalPostService.getTrendingPosts())
                    .build();
        }
    }
}