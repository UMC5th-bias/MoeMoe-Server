package com.favoriteplace.app.dto.community.guestbook;

import com.favoriteplace.app.member.controller.dto.UserInfoResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class GuestBookResponseDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyGuestBookCommentDto{
        private Long page;
        private Long size;
        private List<MyGuestBookComment> comment;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyGuestBookComment{
        private Long id;
        private String content;
        private String passedTime;
        private MyGuestBookInfo myGuestBookInfo;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyGuestBookInfo {
        private Long id;
        private String title;
        private String nickname;
        private Long views;
        private Long likes;
        private Long comments;
        private String passedTime;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyGuestBookDto{
        private Long page;
        private Long size;
        private List<MyGuestBookInfo> myGuestBookInfo;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailGuestBookDto{
        private UserInfoResponseDto userInfo;
        private PilgrimageInfo pilgrimage;
        private GuestBookInfo guestBook;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PilgrimageInfo{
        private String name;
        private Long pilgrimageNumber;
        private Long completeNumber;
        private String address;
        private Double latitude;
        private Double longitude;
        private String imageAnime;
        private String imageReal;
        private String addressEn;
        private String addressJp;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GuestBookInfo{
        private Long id;
        private String title;
        private String content;
        private Long views;
        private Long likes;
        private Long comments;
        private Boolean isLike;
        private Boolean isWrite;
        private String passedTime;
        private List<String> image;
        private List<String> hashTag;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TotalGuestBookDto{
        private Long page;
        private Long size;
        private List<TotalGuestBookInfo> guestBook;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TotalGuestBookInfo{
        private Long id;
        private String title;
        private String nickname;
        private String thumbnail;
        private Long views;
        private Long likes;
        private Long comments;
        private String passedTime;
        private List<String> hashTags;
    }
}