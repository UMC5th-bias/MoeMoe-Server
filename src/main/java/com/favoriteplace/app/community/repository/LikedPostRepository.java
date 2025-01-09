package com.favoriteplace.app.community.repository;

import com.favoriteplace.app.community.domain.LikedPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikedPostRepository extends JpaRepository<LikedPost, Long> {
    Boolean existsByPostIdAndMemberId(Long postId, Long memberId);
    Boolean existsByGuestBookIdAndMemberId(Long guestBookId, Long memberId);

    LikedPost findByPostIdAndMemberId(Long postId, Long memberId);

    void deleteByPostIdAndMemberId(long postId, Long memberId);

    void deleteByGuestBookIdAndMemberId(Long guestBookId, Long memberId);
}
