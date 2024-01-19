package com.favoriteplace.app.repository;

import com.favoriteplace.app.domain.community.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByCreatedAtBetweenOrderByLikeCountDesc(LocalDateTime start, LocalDateTime end);

    Page<Post> findAllByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);
}
