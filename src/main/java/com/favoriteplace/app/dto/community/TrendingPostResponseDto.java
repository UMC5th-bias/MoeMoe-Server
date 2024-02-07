package com.favoriteplace.app.dto.community;

import com.favoriteplace.app.domain.community.GuestBook;
import com.favoriteplace.app.domain.community.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class TrendingPostResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendingTodayPostResponseDto {
        private String date;
        private List<TrendingPostRank> rank;
        }


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendingPostRank {
        private Long id;
        private String title;

        public static TrendingPostRank of(Post post){
            return TrendingPostRank.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .build();
        }

        public static TrendingPostRank of(GuestBook guestBook) {
            return TrendingPostRank.builder()
                    .id(guestBook.getId())
                    .title(guestBook.getTitle())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendingMonthPostResponseDto {
        private Long id;
        private String title;
        private String type;
    }
}
