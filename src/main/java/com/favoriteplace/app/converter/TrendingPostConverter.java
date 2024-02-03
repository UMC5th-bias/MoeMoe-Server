package com.favoriteplace.app.converter;

import com.favoriteplace.app.domain.community.GuestBook;
import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.dto.HomeResponseDto;
import com.favoriteplace.global.util.DateTimeFormatUtils;

import java.util.ArrayList;
import java.util.List;

public class TrendingPostConverter {
    public static HomeResponseDto.TrendingPost toTrendingPost(GuestBook guestBook, int rank, List<String> hashtags){
        return HomeResponseDto.TrendingPost.builder()
                .id(guestBook.getId()).rank(rank).title(guestBook.getTitle())
                .profileImageUrl(guestBook.getMember().getProfileImageUrl())
                .profileIconUrl(guestBook.getMember().getProfileIcon().getImage() != null ? guestBook.getMember().getProfileIcon().getImage().getUrl() : null)
                .hashtags(hashtags)
                .passedTime(DateTimeFormatUtils.getPassDateTime(guestBook.getCreatedAt()))
                .board("성지순례 인증").build();
    }

    public static HomeResponseDto.TrendingPost toTrendingPost(Post post, int rank){
        return HomeResponseDto.TrendingPost.builder()
                .id(post.getId()).rank(rank).title(post.getTitle())
                .profileImageUrl(post.getMember().getProfileImageUrl())
                .profileIconUrl(post.getMember().getProfileIcon().getImage().getUrl())
                .hashtags(new ArrayList<>())
                .passedTime(DateTimeFormatUtils.getPassDateTime(post.getCreatedAt()))
                .board("자유게시판").build();
    }


}
