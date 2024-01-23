package com.favoriteplace.app.repository;

import com.favoriteplace.app.domain.community.LikedPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikedPostRepository extends JpaRepository<LikedPost, Long> {
    Boolean existsByPostIdAndMemberId(Long postId, Long memberId);
}
