package com.favoriteplace.app.dto.community;

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
}