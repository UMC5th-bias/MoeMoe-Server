package com.favoriteplace.app.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


public class MyPageDto {
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MyInfoDto {
        Long doneRally;
        Long visitedPlace;
        Long posts;
        Long comments;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MyProfileDto {
        String nickname;
        String introduction;
        Long point;
        String profileImg;
        String userTitleImg;
        String userIconImg;
        String email;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MyItemDto {
        List<MyItemDetailDto> limited;
        List<MyItemDetailDto> always;
        List<MyItemDetailDto> pilgrimage;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MyItemDetailDto {
        Long id;
        String imageUrl;
        Boolean isWear;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MyGuestBookDto {
        Long id;
        String title;
        Long pilgrimageNumber;
        Long myPilgrimageNumber;
        String imageUrl;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MyBlockDto {
        Long userId;
        String nickname;
        String profileImg;
        String userTitleImg;
        String userIconImg;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyModifyBlockDto{
        Boolean isBlocked;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyFcmTokenDto{
        @NotEmpty
        String fcmToken;
    }
}
