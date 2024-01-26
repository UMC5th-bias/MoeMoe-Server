package com.favoriteplace.app.converter;

import com.favoriteplace.app.domain.Image;
import com.favoriteplace.app.domain.community.GuestBook;
import com.favoriteplace.app.domain.community.HashTag;
import com.favoriteplace.app.domain.travel.Pilgrimage;
import com.favoriteplace.app.dto.community.GuestBookResponseDto;
import com.favoriteplace.global.gcpImage.ConvertUuidToUrl;
import com.favoriteplace.global.util.DateTimeFormatUtils;

import java.util.List;

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
                .imageAnime(ConvertUuidToUrl.convertUuidToUrl(pilgrimage.getVirtualImage().getUrl()))
                .imageReal(ConvertUuidToUrl.convertUuidToUrl(pilgrimage.getRealImage().getUrl()))
                .build();
    }

    public static GuestBookResponseDto.TotalGuestBookInfo toTotalGuestBookInfo(GuestBook guestBook, Image image, Long comments, List<HashTag> hashTags){
        return GuestBookResponseDto.TotalGuestBookInfo.builder()
                .id(guestBook.getId())
                .title(guestBook.getTitle())
                .nickname(guestBook.getMember().getNickname())
                .thumbnail(ConvertUuidToUrl.convertUuidToUrl(image != null ? image.getUrl() : null))
                .views(guestBook.getView())
                .likes(guestBook.getLikeCount())
                .comments(comments)
                .passedTime(DateTimeFormatUtils.getPassDateTime(guestBook.getCreatedAt()))
                .hashTags(hashTags.stream().map(HashTag::getTagName).toList())
                .build();
    }
}
