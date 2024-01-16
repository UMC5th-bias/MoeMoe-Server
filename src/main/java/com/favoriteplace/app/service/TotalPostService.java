package com.favoriteplace.app.service;

import com.favoriteplace.app.domain.community.GuestBook;
import com.favoriteplace.app.domain.community.HashTag;
import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.dto.HomeResponseDto;
import com.favoriteplace.app.repository.GuestBookRepository;
import com.favoriteplace.app.repository.HashtagRepository;
import com.favoriteplace.app.repository.PostRepository;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.util.DateTimeFormatUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TotalPostService {
    private final PostRepository postRepository;
    private final GuestBookRepository guestBookRepository;
    private final HashtagRepository hashtagRepository;

    public List<HomeResponseDto.TrendingPost> getTrendingPosts() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();

        List<GuestBook> todayGuestBooks = guestBookRepository.findByCreatedAtBetweenOrderByLikeCountDesc(startOfDay, now);
        List<Post> todayPosts = postRepository.findByCreatedAtBetweenOrderByLikeCountDesc(startOfDay, now);

        List<Object> combined = new ArrayList<>();
        combined.addAll(todayGuestBooks);
        combined.addAll(todayPosts);

        combined.sort((o1, o2) -> {
            Long likes1 = o1 instanceof GuestBook ? ((GuestBook) o1).getLikeCount() : ((Post) o1).getLikeCount();
            Long likes2 = o2 instanceof GuestBook ? ((GuestBook) o2).getLikeCount() : ((Post) o2).getLikeCount();

            return Long.compare(likes1, likes2);
        });
        combined.subList(0, Math.min(5, combined.size()));

        List<HomeResponseDto.TrendingPost> trendingPosts = new ArrayList<>();

        for(int i=0; i< combined.size() && i<5; i++){
            trendingPosts.add(convertToTrendingPost(combined.get(i), i+1, DateTimeFormatUtils.getPassDateTime(now)));
        }

        return trendingPosts;
    }

    public HomeResponseDto.TrendingPost convertToTrendingPost(Object object, int rank, String passedTime){
        if(object instanceof GuestBook guestBook){
            return HomeResponseDto.TrendingPost.builder()
                    .id(guestBook.getId())
                    .rank(rank)
                    .title(guestBook.getTitle())
                    .profileImageUrl(guestBook.getMember().getProfileImageUrl())
                    .profileIconUrl(guestBook.getMember().getProfile_icon().getImage().getUrl())
                    .hashtags(hashtagRepository.findAllByGuestBookId(guestBook.getId()).stream()
                            .map(HashTag::getTagName)
                            .collect(Collectors.toList()))
                    .passedTime(passedTime)
                    .board("성지순례 인증")
                    .build();
        }
        else if(object instanceof Post){
            Post post = (Post) object;
            return HomeResponseDto.TrendingPost.builder()
                    .id(post.getId())
                    .rank(rank)
                    .title(post.getTitle())
                    .profileImageUrl(post.getMember().getProfileImageUrl())
                    .profileIconUrl(post.getMember().getProfile_icon().getImage().getUrl())
                    .hashtags(new ArrayList<>())
                    .passedTime(passedTime)
                    .board("자유게시판")
                    .build();
        }
        else{
            throw new RestApiException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}