package com.favoriteplace.app.converter;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.dto.community.PostResponseDto;
import com.favoriteplace.global.util.DateTimeFormatUtils;

public class PostConverter {
    public static PostResponseDto.MyPost myPostResponseConverter(Post post, Member member, Long comments){
        return PostResponseDto.MyPost.builder()
                .id(post.getId())
                .title(post.getTitle())
                .nickname(member.getNickname())
                .views(post.getView())
                .likes(post.getLikeCount())
                .comments(comments)
                .passedTime(DateTimeFormatUtils.getPassDateTime(post.getCreatedAt())).build();
    }
}
