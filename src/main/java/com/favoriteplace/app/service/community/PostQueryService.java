package com.favoriteplace.app.service.community;

import com.favoriteplace.app.converter.PostConverter;
import com.favoriteplace.app.domain.Image;
import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.dto.community.PostResponseDto;
import com.favoriteplace.app.dto.community.TrendingPostResponseDto;
import com.favoriteplace.app.repository.CommentRepository;
import com.favoriteplace.app.repository.ImageRepository;
import com.favoriteplace.app.repository.LikedPostRepository;
import com.favoriteplace.app.repository.PostRepository;
import com.favoriteplace.app.service.community.searchStrategy.SearchPostByContent;
import com.favoriteplace.app.service.community.searchStrategy.SearchPostByNickname;
import com.favoriteplace.app.service.community.searchStrategy.SearchPostByTitle;
import com.favoriteplace.app.service.community.searchStrategy.SearchStrategy;
import com.favoriteplace.app.service.community.sortStrategy.SortPostByLatestStrategy;
import com.favoriteplace.app.service.community.sortStrategy.SortPostByLikedStrategy;
import com.favoriteplace.app.service.community.sortStrategy.SortStrategy;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.gcpImage.UploadImage;
import com.favoriteplace.global.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
@Transactional(readOnly = true)
public class PostQueryService {
    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final LikedPostRepository likedPostRepository;
    private final SortPostByLatestStrategy sortPostByLatestStrategy;
    private final SortPostByLikedStrategy sortPostByLikedStrategy;
    private final SearchPostByTitle searchPostByTitle;
    private final SearchPostByNickname searchPostByNickname;
    private final SearchPostByContent searchPostByContent;
    private final SecurityUtil securityUtil;

    /**
     * 자유게시글 전체 글들을 페이징해서 가져오기
     * @param page
     * @param size
     * @param sort
     * @return
     */
    public Page<PostResponseDto.MyPost> getTotalPostBySort(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page-1, size);
        SortStrategy<Post> sortStrategy;
        if("latest".equals(sort)){
            sortStrategy = sortPostByLatestStrategy;
        }else if("liked".equals(sort)) {
            sortStrategy = sortPostByLikedStrategy;
        }else{
            throw new RestApiException(ErrorCode.SORT_TYPE_NOT_ALLOWED);
        }
        Page<Post> sortedPosts = sortStrategy.sort(pageable);
        if(sortedPosts.isEmpty()){return Page.empty();}
        return sortedPosts.map(post -> PostConverter.toMyPost(post, post.getMember(), countPostComment(post)));
    }

    /**
     * searchType(제목, 닉네임, 내용)을 기반으로 게시글을 가져오는 함 (게시글 생성일의 내림차순으로 정렬)
     * @param page
     * @param size
     * @param searchType
     * @param keyword
     * @return
     */
    public Page<PostResponseDto.MyPost> getTotalPostByKeyword(int page, int size, String searchType, String keyword) {
        Pageable pageable = PageRequest.of(page-1, size);
        SearchStrategy<Post> searchStrategy;
        if("title".equals(searchType)){
            searchStrategy = searchPostByTitle;
        } else if ("nickname".equals(searchType)) {
            searchStrategy = searchPostByNickname;
        } else if ("content".equals(searchType)) {
            searchStrategy = searchPostByContent;
        } else {
            throw new RestApiException(ErrorCode.SEARCH_TYPE_NOT_ALLOWED);
        }
        if(keyword.trim().isEmpty()){return Page.empty();}
        Page<Post> postPage = searchStrategy.search(keyword, pageable);
        if(postPage.isEmpty()){return Page.empty();}
        return postPage.map(post -> PostConverter.toMyPost(post, post.getMember(), countPostComment(post)));
    }

    /**
     * 내가 작성한 글들 페이징 해서 보여주는 함수
     * @param page
     * @param size
     * @return
     */
    public Page<PostResponseDto.MyPost> getMyPosts(Member member, int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<Post> postPage = postRepository.findAllByMemberIdOrderByCreatedAtDesc(member.getId(), pageable);
        if(postPage.isEmpty()){return Page.empty();}
        return postPage.map(post -> PostConverter.toMyPost(post, member, countPostComment(post)));
    }

    /**
     * 당일 자유게시판 인기글 상위 5개를 보여주는 함수
     * @return
     */
    public List<TrendingPostResponseDto.TrendingPostRank> getTodayTrendingPost() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        List<Post> posts = postRepository.findByCreatedAtBetweenOrderByLikeCountDesc(startOfDay, now);
        if(posts.isEmpty()){
            throw new RestApiException(ErrorCode.POST_NOT_FOUND);
        }
        posts.subList(0, Math.min(5, posts.size()));

        List<TrendingPostResponseDto.TrendingPostRank> trendingPostRanks = new ArrayList<>();
        for(int i =0; i<posts.size() && i<5; i++){
            trendingPostRanks.add(TrendingPostResponseDto.TrendingPostRank.of(posts.get(i)));
        }
        return trendingPostRanks;
    }

    public PostResponseDto.PostInfo getPostDetail(Long postId, HttpServletRequest request) {
        Optional<Post> post = postRepository.findById(postId);
        if(post.isEmpty()){
            throw new RestApiException(ErrorCode.POST_NOT_FOUND);
        }
        List<String> imageUrls = getImageUrlsByPostId(postId);
        if(!securityUtil.isTokenExists(request)){
            return PostResponseDto.PostInfo.of(post.get(), false, false, imageUrls);
        }
        Long memberId = securityUtil.getUserFromHeader(request).getId();
        return PostResponseDto.PostInfo.of(post.get(), isLiked(postId, memberId), isWriter(postId, memberId), imageUrls);
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

    /**
     * 게시글의 댓글이 몇개인지 counting하는 함수
     * @param post
     * @return
     */
    public Long countPostComment(Post post){
        return (long) post.getComments().size();
        //return commentRepository.countByPostId(post.getId()) != null ? commentRepository.countByPostId(post.getId()) : 0L;
    }

    /**
     * 게시글의 조회수를 증가하는 함수
     * @param postId
     */
    public void increasePostView(Long postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if(postOptional.isEmpty()){throw new RestApiException(ErrorCode.POST_NOT_FOUND);}
        Post post = postOptional.get();
        post.increaseView();
        postRepository.save(post);
    }

}