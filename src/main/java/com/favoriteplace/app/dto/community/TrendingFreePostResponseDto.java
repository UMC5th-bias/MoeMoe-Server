package com.favoriteplace.app.dto.community;

import com.favoriteplace.app.domain.community.Post;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TrendingFreePostResponseDto {
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

    }
}