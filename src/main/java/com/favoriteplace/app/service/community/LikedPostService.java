package com.favoriteplace.app.service.community;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.community.GuestBook;
import com.favoriteplace.app.domain.community.LikedPost;
import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.repository.GuestBookRepository;
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
    private final GuestBookRepository guestBookRepository;

    /**
     * 자유게시글 추천(좋아요) 기능
     * @param member
     * @param postId
     * @return
     */
    @Transactional
    public String modifyPostLike(Member member, long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RestApiException(ErrorCode.POST_NOT_FOUND));
        LikedPost likedPost = likedPostRepository.findByPostIdAndMemberId(post.getId(), member.getId());
        if(likedPost != null){
            post.decreaseLikeCount();
            likedPostRepository.delete(likedPost);
            return "추천을 취소했습니다.";
        }else{
            likedPost = LikedPost.builder()
                    .member(member).post(post).build();
            post.increaseLikeCount();
            likedPostRepository.save(likedPost);
            return "추천되었습니다.";
        }
    }

    /**
     * 성지순례 인증글 추천(좋아요) 함수
     * @param member
     * @param guestbookId
     * @return
     */
    @Transactional
    public String modifyGuestBookLike(Member member, Long guestbookId) {
        GuestBook guestBook = guestBookRepository.findById(guestbookId).orElseThrow(() -> new RestApiException(ErrorCode.GUESTBOOK_NOT_FOUND));
        Boolean likeExists = likedPostRepository.existsByGuestBookIdAndMemberId(guestBook.getId(), member.getId());
        if(likeExists){
            likedPostRepository.deleteByGuestBookIdAndMemberId(guestBook.getId(), member.getId());
            return "추천을 취소했습니다.";
        }else{
            LikedPost likedPost = LikedPost.builder().member(member).guestBook(guestBook).build();
            likedPostRepository.save(likedPost);
            return "추천을 완료했습니다.";
        }
    }
}
