package com.favoriteplace.app.service;

import com.favoriteplace.app.converter.TrendingPostConverter;
import com.favoriteplace.app.domain.community.GuestBook;
import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.dto.HomeResponseDto;
import com.favoriteplace.app.dto.community.TrendingPostResponseDto;
import com.favoriteplace.app.repository.GuestBookImplRepository;
import com.favoriteplace.app.repository.PostImplRepository;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TotalPostService {
    private final PostImplRepository postImplRepository;
    private final GuestBookImplRepository guestBookImplRepository;

    public List<HomeResponseDto.TrendingPost> getTrendingPosts() {
        List<Object> combinedPosts = getNTrendingPosts(5, "day");
        List<HomeResponseDto.TrendingPost> trendingPosts = new ArrayList<>();
        for(int i=0; i< combinedPosts.size() && i<5; i++){
            trendingPosts.add(convertToTodayTrendingPost(combinedPosts.get(i), i+1));
        }
        return trendingPosts;
    }

    public List<TrendingPostResponseDto.TrendingMonthPostResponseDto> getMonthTrendingPosts() {
        List<Object> combinedPosts = getNTrendingPosts(3, "month");
        return combinedPosts.stream()
                .limit(3)
                .map(this::convertToMonthTrendingPost).collect(Collectors.toList());
    }

    //postNumber : 보여줄 게시글 갯수, period : "day" or "month"
    public List<Object> getNTrendingPosts(int postNumber, String period){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDateTime;

        if ("day".equals(period)) {
            startDateTime = now.toLocalDate().atStartOfDay();
        } else if ("month".equals(period)) {
            startDateTime = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
        } else {
            throw new IllegalArgumentException("Invalid period: " + period);
        }

        List<GuestBook> todayGuestBooks = guestBookImplRepository.findByCreatedAtBetweenOrderByLikeCountDesc(startDateTime, now, postNumber);
        List<Post> todayPosts = postImplRepository.findByCreatedAtBetweenOrderByLikeCountDesc(startDateTime, now, postNumber);

        List<Object> combined = new ArrayList<>();
        combined.addAll(todayGuestBooks);
        combined.addAll(todayPosts);

        combined.sort((o1, o2) -> {
            Long likes1 = o1 instanceof GuestBook ? ((GuestBook) o1).getLikeCount() : ((Post) o1).getLikeCount();
            Long likes2 = o2 instanceof GuestBook ? ((GuestBook) o2).getLikeCount() : ((Post) o2).getLikeCount();

            return Long.compare(likes2, likes1);
        });
        combined.subList(0, Math.min(postNumber, combined.size()));
        return combined;
    }


    private HomeResponseDto.TrendingPost convertToTodayTrendingPost(Object object, int rank){
        if(object instanceof GuestBook guestBook){
            return TrendingPostConverter.toGuestBookTrending(guestBook, rank);
        }
        else if(object instanceof Post post){
            return TrendingPostConverter.toGuestBookTrending(post, rank);
        }
        else{
            throw new RestApiException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private TrendingPostResponseDto.TrendingMonthPostResponseDto convertToMonthTrendingPost(Object o) {
        if(o instanceof GuestBook guestBook){
            return TrendingPostResponseDto.TrendingMonthPostResponseDto.builder()
                    .id(guestBook.getId()).title(guestBook.getTitle())
                    .type("성지순례 인증").build();
        }
        else if(o instanceof Post post){
            return TrendingPostResponseDto.TrendingMonthPostResponseDto.builder()
                    .id(post.getId()).title(post.getTitle())
                    .type("자유게시판").build();
        }
        else{
            throw new RestApiException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

}