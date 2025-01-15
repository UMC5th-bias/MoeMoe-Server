package com.favoriteplace.app.community.controller.dto;

import com.favoriteplace.app.rally.domain.Rally;
import com.favoriteplace.app.member.controller.dto.UserInfoResponseDto;
import lombok.*;

import java.util.List;

@Getter
@Builder
public class HomeResponseDto {
    private Boolean isLoggedIn;
    private UserInfoResponseDto userInfo;
    private HomeRally rally;
    private List<TrendingPost> trendingPosts;

    @Getter
    @Builder
    public static class HomeRally{
        private Long id;
        private String name;
        private String backgroundImageUrl;
        private Long pilgrimageNumber;
        private Long completeNumber;

        public static HomeRally of(Rally rally, Long completeNumber){
            return HomeRally.builder()
                    .id(rally.getId())
                    .name(rally.getName())
                    .backgroundImageUrl(rally.getImage().getUrl())
                    .pilgrimageNumber(rally.getPilgrimageNumber())
                    .completeNumber(completeNumber)
                    .build();
        }

    }

    @Getter
    @Builder
    public static class TrendingPost{
        private Long id;
        private int rank;
        private String title;
        private String profileImageUrl;
        private String profileIconUrl;
        private List<String> hashtags;
        private String passedTime;
        private String board;
    }
}