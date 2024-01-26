package com.favoriteplace.app.dto.community;

import com.favoriteplace.app.dto.UserInfoResponseDto;
import com.favoriteplace.app.dto.travel.PilgrimageDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GuestBookResponseDto {

    @Builder
    @Getter
    public static class MyGuestBookCommentDto{
        private Long page;
        private Long size;
        private List<MyGuestBookComment> comment;
    }

    @Builder
    @Getter
    public static class MyGuestBookComment{
        private Long id;
        private String content;
        private String passedTime;
        private GuestBook guestBook;
    }

    @Builder
    @Getter
    public static class GuestBook{
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
    public static class MyGuestBookDto{
        private Long page;
        private Long size;
        private List<GuestBook> guestBook;
    }

    @Getter
    @Builder
    public static class DetailGuestBookDto{
        private UserInfoResponseDto userInfo;
        private PilgrimageInfo pilgrimage;
        private GuestBookInfo guestBook;
    }

    @Getter
    @Builder
    public static class PilgrimageInfo{
        private String name;
        private Long pilgrimageNumber;
        private Long completeNumber;
        private String address;
        private Double latitude;
        private Double longitude;
        private String imageAnime;
        private String imageReal;
    }

    @Getter
    @Builder
    public static class GuestBookInfo{
        private Long id;
        private String title;
        private String content;
        private Long views;
        private Long likes;
        private Boolean isLike;
        private Boolean isWrite;
        private String passedTime;
        private List<String> image;
        private List<String> hashTag;
    }
}