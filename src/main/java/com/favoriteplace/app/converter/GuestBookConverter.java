package com.favoriteplace.app.converter;

import com.favoriteplace.app.domain.community.GuestBook;
import com.favoriteplace.app.domain.community.HashTag;
import com.favoriteplace.app.domain.travel.Pilgrimage;
import com.favoriteplace.app.dto.community.GuestBookResponseDto;
import com.favoriteplace.global.util.DateTimeFormatUtils;

import java.util.List;

public class GuestBookConverter {
    public static GuestBookResponseDto.MyGuestBookInfo toGuestBook(GuestBook guestBook, String nickname){
        return GuestBookResponseDto.MyGuestBookInfo.builder()
                .id(guestBook.getId())
                .title(guestBook.getTitle())
                .nickname(nickname)
                .views(guestBook.getView())
                .likes(guestBook.getLikeCount())
                .comments((long) guestBook.getComments().size())
                .passedTime(DateTimeFormatUtils.getPassDateTime(guestBook.getCreatedAt()))
                .build();
    }

    public static GuestBookResponseDto.GuestBookInfo toGuestBookInfo(GuestBook guestBook, Boolean isLike, Boolean isWrite, List<String> images, List<String> hashtags){
        return GuestBookResponseDto.GuestBookInfo.builder()
                .id(guestBook.getId())
                .title(guestBook.getTitle())
                .content(guestBook.getContent())
                .views(guestBook.getView())
                .likes(guestBook.getLikeCount())
                .isLike(isLike)
                .isWrite(isWrite)
                .passedTime(DateTimeFormatUtils.getPassDateTime(guestBook.getCreatedAt()))
                .image(images)
                .hashTag(hashtags)
                .build();
    }

    public static GuestBookResponseDto.PilgrimageInfo toPilgrimageInfo(Pilgrimage pilgrimage, Long pilgrimageNumber, Long completeNumber){
        return GuestBookResponseDto.PilgrimageInfo.builder()
                .name(pilgrimage.getRallyName())
                .pilgrimageNumber(pilgrimageNumber)
                .completeNumber(completeNumber)
                .address(pilgrimage.getDetailAddress())
                .latitude(pilgrimage.getLatitude())
                .longitude(pilgrimage.getLongitude())
                .imageAnime(pilgrimage.getVirtualImage().getUrl())
                .imageReal(pilgrimage.getRealImage().getUrl())
                .build();
    }

    public static GuestBookResponseDto.TotalGuestBookInfo toTotalGuestBookInfo(GuestBook guestBook){
        return GuestBookResponseDto.TotalGuestBookInfo.builder()
                .id(guestBook.getId())
                .title(guestBook.getTitle())
                .nickname(guestBook.getMember().getNickname())
                .thumbnail(!guestBook.getImages().isEmpty() ? guestBook.getImages().get(0).getUrl() : null)
                .views(guestBook.getView())
                .likes(guestBook.getLikeCount())
                .comments((long) guestBook.getComments().size())
                .passedTime(DateTimeFormatUtils.getPassDateTime(guestBook.getCreatedAt()))
                .hashTags(guestBook.getHashTags().stream().map(HashTag::getTagName).toList())
                .build();
    }
}
