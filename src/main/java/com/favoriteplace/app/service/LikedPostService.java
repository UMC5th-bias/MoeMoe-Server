package com.favoriteplace.app.service;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.community.LikedPost;
import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.repository.LikedPostRepository;
import com.favoriteplace.app.repository.PostRepository;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikedPostService {
    private final LikedPostRepository likedPostRepository;
    private final PostRepository postRepository;
    private final SecurityUtil securityUtil;

    @Transactional
    public String modifyPostLike(long postId) {
        Member member = securityUtil.getUser();
        Post post = postRepository.findById(postId).orElseThrow(() -> new RestApiException(ErrorCode.POST_NOT_FOUND));
        LikedPost likedPost = likedPostRepository.findByPostIdAndMemberId(post.getId(), member.getId());
        if(likedPost != null){
            likedPostRepository.delete(likedPost);
            return "추천을 취소했습니다.";
        }else{
            likedPost = LikedPost.builder()
                    .member(member).post(post).build();
            likedPostRepository.save(likedPost);
            return "추천되었습니다.";
        }
    }
}
