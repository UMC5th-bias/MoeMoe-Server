package com.favoriteplace.app.service;

import com.favoriteplace.app.domain.community.GuestBook;
import com.favoriteplace.app.dto.community.TrendingPostResponseDto;
import com.favoriteplace.app.repository.GuestBookRepository;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GuestBookService {
    private final GuestBookRepository guestBookRepository;

    @Transactional
    public List<TrendingPostResponseDto.TrendingTodayPostResponseDto.TrendingPostRank> getTodayTrendingGuestBook() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        List<GuestBook> guestBooks = guestBookRepository.findByCreatedAtBetweenOrderByLikeCountDesc(startOfDay, now);
        if(guestBooks.isEmpty()){
            throw new RestApiException(ErrorCode.GUESTBOOK_NOT_FOUND);
        }
        guestBooks.subList(0, Math.min(5, guestBooks.size()));

        List<TrendingPostResponseDto.TrendingTodayPostResponseDto.TrendingPostRank> trendingPostsRank = new ArrayList<>();
        for(int i = 0; (i < guestBooks.size()) && (i < 5); i++){
            trendingPostsRank.add(TrendingPostResponseDto.TrendingTodayPostResponseDto.TrendingPostRank.of(guestBooks.get(i)));
        }
        return trendingPostsRank;
    }
}
