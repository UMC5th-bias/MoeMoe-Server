package com.favoriteplace.app.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;


public class MyPageDto {
    public static class MyInfoDto {
        Long doneRally;
        Long visitedPlace;
        Long posts;
        Long comments;
    }

    public static class MyProfileDto {
        String nickname;
        String introduction;
        Long point;
        String profileImg;
        String userTitleImg;
        String userIconImg;
        String email;
    }

    public static class MyItemDto {
        List<MyItemDetailDto> limited;
        List<MyItemDetailDto> always;
        List<MyItemDetailDto> pilgrimage;
    }

    public static class MyItemDetailDto {
        Long id;
        String imageUrl;
    }

    public static class MyGuestBookDto {
        Long id;
        String title;
        Long pilgrimageNumber;
        Long myPilgrimageNumber;
        String imageUrl;
    }


    public static class MyBlockDto {
        Long userId;
        String nickname;
        String profileImg;
        String userTitleImg;
        String userIconImg;
    }
}
