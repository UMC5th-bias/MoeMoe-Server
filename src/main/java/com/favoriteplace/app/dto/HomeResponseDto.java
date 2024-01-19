package com.favoriteplace.app.dto;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.travel.Rally;
import lombok.*;

import java.util.List;

@Getter
@Builder
public class HomeResponseDto {
    private Boolean isLoggedIn;
    private UserInfo userInfo;
    private HomeRally rally;
    private List<TrendingPost> trendingPosts;

    @Getter
    @Builder
    public static class UserInfo{
        private Long id;
        private String nickname;
        private String profileImageUrl;
        private String profileTitleUrl;
        private String profileIconUrl;

        public static UserInfo of(Member member){
            return UserInfo.builder()
                    .id(member.getId())
                    .nickname(member.getNickname())
                    .profileImageUrl(member.getProfileImageUrl())
                    .profileTitleUrl(member.getProfileTitle().getImage().getUrl())
                    .profileIconUrl(member.getProfileIcon().getImage().getUrl())
                    .build();
        }
    }

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