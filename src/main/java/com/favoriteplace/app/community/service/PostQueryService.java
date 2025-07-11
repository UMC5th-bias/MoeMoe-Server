package com.favoriteplace.app.community.service;

import com.favoriteplace.app.community.converter.PostConverter;
import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.community.domain.Post;
import com.favoriteplace.app.community.controller.dto.PostResponseDto;
import com.favoriteplace.app.community.controller.dto.TrendingPostResponseDto;
import com.favoriteplace.app.community.repository.LikedPostRepository;
import com.favoriteplace.app.community.repository.PostImplRepository;
import com.favoriteplace.app.community.service.searchStrategy.SearchPostByContent;
import com.favoriteplace.app.community.service.searchStrategy.SearchPostByNickname;
import com.favoriteplace.app.community.service.searchStrategy.SearchPostByTitle;
import com.favoriteplace.app.community.service.searchStrategy.SearchStrategy;
import com.favoriteplace.app.community.service.sortStrategy.SortPostByLatestStrategy;
import com.favoriteplace.app.community.service.sortStrategy.SortPostByLikedStrategy;
import com.favoriteplace.app.community.service.sortStrategy.SortStrategy;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryService {
    private final PostImplRepository postImplRepository;
    private final LikedPostRepository likedPostRepository;
    private final SortPostByLatestStrategy sortPostByLatestStrategy;
    private final SortPostByLikedStrategy sortPostByLikedStrategy;
    private final SearchPostByTitle searchPostByTitle;
    private final SearchPostByNickname searchPostByNickname;
    private final SearchPostByContent searchPostByContent;
    private final SecurityUtil securityUtil;

    /**
     * 자유게시글 전체 글들을 페이징해서 가져오기
     *
     * @param page
     * @param size
     * @param sort
     * @return
     */
    public List<PostResponseDto.MyPost> getTotalPostBySort(int page, int size, String sort) {
        SortStrategy<Post> sortStrategy;
        if ("latest".equals(sort)) {
            sortStrategy = sortPostByLatestStrategy;
        } else if ("liked".equals(sort)) {
            sortStrategy = sortPostByLikedStrategy;
        } else {
            throw new RestApiException(ErrorCode.SORT_TYPE_NOT_ALLOWED);
        }
        List<Post> sortedPosts = sortStrategy.sort(page, size);
        if (sortedPosts.isEmpty()) {
            return Collections.emptyList();
        }
        return sortedPosts.stream()
                .map(PostConverter::toMyPost)
                .collect(Collectors.toList());
    }

    /**
     * searchType(제목, 닉네임, 내용)을 기반으로 게시글을 가져오는 함 (게시글 생성일의 내림차순으로 정렬)
     *
     * @param page
     * @param size
     * @param searchType
     * @param keyword
     * @return
     */
    public List<PostResponseDto.MyPost> getTotalPostByKeyword(
            int page, int size, String searchType, String keyword
    ) {
        SearchStrategy<Post> searchStrategy;
        if ("title".equals(searchType)) {
            searchStrategy = searchPostByTitle;
        } else if ("nickname".equals(searchType)) {
            searchStrategy = searchPostByNickname;
        } else if ("content".equals(searchType)) {
            searchStrategy = searchPostByContent;
        } else {
            throw new RestApiException(ErrorCode.SEARCH_TYPE_NOT_ALLOWED);
        }
        if (keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<Post> postPage = searchStrategy.search(keyword, page, size);
        if (postPage.isEmpty()) {
            return Collections.emptyList();
        }
        return postPage.stream()
                .map(PostConverter::toMyPost)
                .toList();
    }

    /**
     * 내가 작성한 글들 페이징 해서 보여주는 함수
     *
     * @param page
     * @param size
     * @return
     */
    public List<PostResponseDto.MyPost> getMyPosts(Member member, int page, int size) {
        List<Post> postPage = postImplRepository.findAllByMemberIdOrderByCreatedAtDesc(member.getId(), page, size);
        if (postPage.isEmpty()) {
            return Collections.emptyList();
        }
        return postPage.stream()
                .map(PostConverter::toMyPost)
                .toList();
    }

    /**
     * 당일 자유게시판 인기글 상위 5개를 보여주는 함수
     *
     * @return
     */
    public List<TrendingPostResponseDto.TrendingPostRank> getTodayTrendingPost() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        List<Post> posts = postImplRepository.findByCreatedAtBetweenOrderByLikeCountDesc(startOfDay, now, 5);
        if (posts.isEmpty()) {
            throw new RestApiException(ErrorCode.POST_NOT_FOUND);
        }
        posts.subList(0, Math.min(5, posts.size()));

        List<TrendingPostResponseDto.TrendingPostRank> trendingPostRanks = new ArrayList<>();
        for (int i = 0; i < posts.size() && i < 5; i++) {
            trendingPostRanks.add(TrendingPostResponseDto.TrendingPostRank.of(posts.get(i)));
        }
        return trendingPostRanks;
    }

    /**
     * 자유 게시글 상제 정보 조회
     *
     * @param postId
     * @param request
     * @return
     */
    public PostResponseDto.PostDetailResponseDto getPostDetail(Long postId, HttpServletRequest request) {
        Post post = postImplRepository.findOneById(postId);
        if (post == null) {
            throw new RestApiException(ErrorCode.POST_NOT_FOUND);
        }
        if (!securityUtil.isTokenExists(request)) {
            return PostConverter.toPostDetailResponse(post, false, false);
        }
        Long memberId = securityUtil.getUserFromHeader(request).getId();
        return PostConverter.toPostDetailResponse(post, isLiked(postId, memberId), isWriter(post, memberId));
    }

    /**
     * 사용자가 해당 글의 작성자가 맞는지 확인
     *
     * @param post
     * @param memberId
     * @return
     */
    private Boolean isWriter(Post post, Long memberId) {
        return post.getMember().getId().equals(memberId);
    }

    /**
     * 사용자가 해당 글에 좋아요를 눌렀는지 확인
     *
     * @param postId
     * @param memberId
     * @return
     */
    private Boolean isLiked(Long postId, Long memberId) {
        return likedPostRepository.existsByPostIdAndMemberId(postId, memberId);
    }

}