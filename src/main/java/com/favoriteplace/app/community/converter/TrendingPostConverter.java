package com.favoriteplace.app.community.converter;

import com.favoriteplace.app.community.domain.GuestBook;
import com.favoriteplace.app.community.domain.HashTag;
import com.favoriteplace.app.community.domain.Post;
import com.favoriteplace.app.dto.HomeResponseDto;
import com.favoriteplace.global.util.DateTimeFormatUtils;

import java.util.ArrayList;

public class TrendingPostConverter {
    public static HomeResponseDto.TrendingPost toGuestBookTrending(GuestBook guestBook, int rank) {
        return HomeResponseDto.TrendingPost.builder()
                .id(guestBook.getId()).rank(rank).title(guestBook.getTitle())
                .profileImageUrl(guestBook.getMember().getProfileImageUrl())
                .profileIconUrl(
                        guestBook.getMember().getProfileIcon() != null
                                ? guestBook.getMember().getProfileIcon().getDefaultImage().getUrl() : null
                )
                .hashtags(guestBook.getHashTags().stream().map(HashTag::getTagName).toList())
                .passedTime(DateTimeFormatUtils.getPassDateTime(guestBook.getCreatedAt()))
                .board("성지순례 인증").build();
    }

    public static HomeResponseDto.TrendingPost toGuestBookTrending(Post post, int rank) {
        return HomeResponseDto.TrendingPost.builder()
                .id(post.getId()).rank(rank).title(post.getTitle())
                .profileImageUrl(post.getMember().getProfileImageUrl())
                .profileIconUrl(
                        post.getMember().getProfileIcon() != null
                                ? post.getMember().getProfileIcon().getDefaultImage().getUrl() : null
                )
                .hashtags(new ArrayList<>())
                .passedTime(DateTimeFormatUtils.getPassDateTime(post.getCreatedAt()))
                .board("자유게시판").build();
    }


}
