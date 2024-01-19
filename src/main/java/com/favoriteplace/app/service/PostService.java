package com.favoriteplace.app.service;

import com.favoriteplace.app.domain.Image;
import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.dto.community.PostResponseDto;
import com.favoriteplace.app.dto.community.TrendingPostResponseDto;
import com.favoriteplace.app.repository.ImageRepository;
import com.favoriteplace.app.repository.LikedPostRepository;
import com.favoriteplace.app.repository.PostRepository;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final LikedPostRepository likedPostRepository;
    private final SecurityUtil securityUtil;

    @Transactional
    public List<TrendingPostResponseDto.TrendingTodayPostResponseDto.TrendingPostRank> getTodayTrendingPost() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        List<Post> posts = postRepository.findByCreatedAtBetweenOrderByLikeCountDesc(startOfDay, now);
        if(posts.isEmpty()){
            throw new RestApiException(ErrorCode.POST_NOT_FOUND);
        }
        posts.subList(0, Math.min(5, posts.size()));

        List<TrendingPostResponseDto.TrendingTodayPostResponseDto.TrendingPostRank> trendingPostRanks = new ArrayList<>();
        for(int i =0; i<posts.size() && i<5; i++){
            trendingPostRanks.add(TrendingPostResponseDto.TrendingTodayPostResponseDto.TrendingPostRank.of(posts.get(i)));
        }
        return trendingPostRanks;
    }

    @Transactional
    public PostResponseDto.PostInfoResponseDto getPostDetail(Long postId, HttpServletRequest request) {
        Optional<Post> post = postRepository.findById(postId);
        if(post.isEmpty()){
            throw new RestApiException(ErrorCode.POST_NOT_FOUND);
        }
        List<String> imageUrls = getImageUrlsByPostId(postId);
        if(!securityUtil.isTokenExists(request)){
            return PostResponseDto.PostInfoResponseDto.of(post.get(), false, false, imageUrls);
        }
        Long memberId = securityUtil.getUserFromHeader(request).getId();
        return PostResponseDto.PostInfoResponseDto.of(post.get(), isLiked(postId, memberId), isWriter(postId, memberId), imageUrls);
    }

    private Boolean isWriter(Long postId, Long memberId){
        Optional<Post> optionalPost = postRepository.findById(postId);
        if(optionalPost.isEmpty()){
            throw new RestApiException(ErrorCode.POST_NOT_FOUND);
        }
        return optionalPost.get().getMember().getId().equals(memberId);
    }

    private Boolean isLiked(Long postId, Long memberId){
        return likedPostRepository.existsByPostIdAndMemberId(postId, memberId);
    }

    private List<String> getImageUrlsByPostId(Long postId) {
        return Optional.ofNullable(imageRepository.findByPostId(postId))
                .map(images -> images.stream().map(Image::getUrl).collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

}
