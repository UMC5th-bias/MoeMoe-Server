package com.favoriteplace.app.controller;

import com.favoriteplace.app.dto.HomeResponseDto;
import com.favoriteplace.app.service.MemberService;
import com.favoriteplace.app.service.RallyService;
import com.favoriteplace.app.service.TotalPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
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
    public HomeResponseDto getHomeInfo(@RequestHeader("Authorization") String accessToken){
        //유저가 맞을 때
        if(memberService.isTokenExists(accessToken)){
            return HomeResponseDto.builder()
                    .isLoggedIn(true)
                    .userInfo(memberService.getUserInfo(accessToken))
                    .rally(rallyService.getMemberRecentRally(accessToken))
                    .trendingPosts(totalPostService.getTrendingPosts())
                    .build();
        }
        //유저가 아닐때
        else{
            return HomeResponseDto.builder()
                    .isLoggedIn(false)
                    .userInfo(null)
                    .rally(rallyService.getRandomRally())
                    .trendingPosts(totalPostService.getTrendingPosts())
                    .build();
        }
    }

}
