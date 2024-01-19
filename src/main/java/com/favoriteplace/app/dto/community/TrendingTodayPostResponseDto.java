package com.favoriteplace.app.dto.community;

import com.favoriteplace.app.domain.community.GuestBook;
import com.favoriteplace.app.domain.community.Post;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TrendingTodayPostResponseDto {
    private String date;
    private List<TrendingPostRank> rank;

    @Getter
    @Builder
    public static class TrendingPostRank{
        private Long id;
        private String title;

        public static TrendingPostRank of(Post post){
            return TrendingPostRank.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .build();
        }

        public static TrendingPostRank of(GuestBook guestBook){
            return TrendingPostRank.builder()
                    .id(guestBook.getId())
                    .title(guestBook.getContent())
                    .build();
        }

    }
}