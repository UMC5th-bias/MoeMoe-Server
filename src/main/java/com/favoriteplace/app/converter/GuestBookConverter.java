package com.favoriteplace.app.converter;

import com.favoriteplace.app.domain.community.GuestBook;
import com.favoriteplace.app.dto.community.GuestBookResponseDto;
import com.favoriteplace.global.util.DateTimeFormatUtils;

public class GuestBookConverter {
    public static GuestBookResponseDto.GuestBook toGuestBook(GuestBook guestBook, String nickname, Long comments){
        return GuestBookResponseDto.GuestBook.builder()
                .id(guestBook.getId())
                .title(guestBook.getTitle())
                .nickname(nickname)
                .views(guestBook.getView())
                .likes(guestBook.getLikeCount())
                .comments(comments)
                .passedTime(DateTimeFormatUtils.getPassDateTime(guestBook.getCreatedAt()))
                .build();
    }
}
