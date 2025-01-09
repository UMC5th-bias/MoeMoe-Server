package com.favoriteplace.app.community.converter;

import com.favoriteplace.app.domain.Image;
import com.favoriteplace.app.community.domain.Comment;
import com.favoriteplace.app.community.domain.GuestBook;
import com.favoriteplace.app.community.domain.HashTag;
import com.favoriteplace.app.domain.travel.Pilgrimage;
import com.favoriteplace.app.member.controller.dto.UserInfoResponseDto;
import com.favoriteplace.app.community.controller.dto.guestbook.GuestBookResponseDto;
import com.favoriteplace.global.util.DateTimeFormatUtils;

import java.util.List;

public class GuestBookConverter {
    public static GuestBookResponseDto.MyGuestBookInfo toGuestBook(GuestBook guestBook) {
        return GuestBookResponseDto.MyGuestBookInfo.builder()
                .id(guestBook.getId())
                .title(guestBook.getTitle())
                .nickname(guestBook.getMember().getNickname())
                .views(guestBook.getView())
                .likes(guestBook.getLikeCount())
                .comments(getNotDeletedComment(guestBook.getComments()))
                .passedTime(DateTimeFormatUtils.getPassDateTime(guestBook.getCreatedAt()))
                .build();
    }

    public static GuestBookResponseDto.GuestBookInfo toGuestBookInfo(
            GuestBook guestBook, Boolean isLike, Boolean isWrite
    ) {
        return GuestBookResponseDto.GuestBookInfo.builder()
                .id(guestBook.getId())
                .title(guestBook.getTitle())
                .content(guestBook.getContent())
                .views(guestBook.getView())
                .likes(guestBook.getLikeCount())
                .comments(getNotDeletedComment(guestBook.getComments()))
                .isLike(isLike)
                .isWrite(isWrite)
                .passedTime(DateTimeFormatUtils.getPassDateTime(guestBook.getCreatedAt()))
                .image(guestBook.getImages().stream().map(Image::getUrl).toList())
                .hashTag(guestBook.getHashTags().stream().map(HashTag::getTagName).toList())
                .build();
    }

    public static GuestBookResponseDto.PilgrimageInfo toPilgrimageInfo(
            Pilgrimage pilgrimage, Long completeNumber
    ) {
        return GuestBookResponseDto.PilgrimageInfo.builder()
                .name(pilgrimage.getRallyName())
                .pilgrimageNumber(pilgrimage.getRally().getPilgrimageNumber())
                .completeNumber(completeNumber)
                .address(pilgrimage.getDetailAddress())
                .latitude(pilgrimage.getLatitude())
                .longitude(pilgrimage.getLongitude())
                .imageAnime(pilgrimage.getVirtualImage().getUrl())
                .imageReal(pilgrimage.getRealImage().getUrl())
                .addressEn(pilgrimage.getDetailAddressEn())
                .addressJp(pilgrimage.getDetailAddressJp())
                .build();
    }

    public static GuestBookResponseDto.TotalGuestBookInfo toTotalGuestBookInfo(GuestBook guestBook) {
        return GuestBookResponseDto.TotalGuestBookInfo.builder()
                .id(guestBook.getId())
                .title(guestBook.getTitle())
                .nickname(guestBook.getMember().getNickname())
                .thumbnail(!guestBook.getImages().isEmpty() ? guestBook.getImages().get(0).getUrl() : null)
                .views(guestBook.getView())
                .likes(guestBook.getLikeCount())
                .comments(getNotDeletedComment(guestBook.getComments()))
                .passedTime(DateTimeFormatUtils.getPassDateTime(guestBook.getCreatedAt()))
                .hashTags(guestBook.getHashTags().stream().map(HashTag::getTagName).toList())
                .build();
    }

    public static GuestBookResponseDto.DetailGuestBookDto toDetailGuestBookInfo(
            GuestBook guestBook,
            boolean isLike,
            boolean isWrite,
            GuestBookResponseDto.PilgrimageInfo pilgrimageInfo
    ) {
        return GuestBookResponseDto.DetailGuestBookDto.builder()
                .userInfo(UserInfoResponseDto.of(guestBook.getMember()))
                .pilgrimage(pilgrimageInfo)
                .guestBook(toGuestBookInfo(guestBook, isLike, isWrite))
                .build();

    }

    private static long getNotDeletedComment(List<Comment> comments) {
        return comments.stream()
                .filter(comment -> !comment.isDeleted())
                .count();
    }

}
