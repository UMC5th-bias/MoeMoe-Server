package com.favoriteplace.app.controller;

import com.favoriteplace.app.dto.HomeResponseDto;
import com.favoriteplace.app.service.MemberService;
import com.favoriteplace.app.service.RallyService;
import com.favoriteplace.app.service.TotalPostService;
import com.favoriteplace.global.util.SecurityUtil;
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
    private final SecurityUtil securityUtil;
    private final RallyService rallyService;
    private final TotalPostService totalPostService;

    @GetMapping()
    public HomeResponseDto getHomeInfo(HttpServletRequest request) {
        return HomeResponseDto.builder()
                .isLoggedIn(securityUtil.isTokenExists(request))
                .userInfo(memberService.getUserInfo(request))
                .rally(rallyService.getRecentRallyElseRandomRally(request))
                .trendingPosts(totalPostService.getTrendingPosts())
                .build();
    }
}