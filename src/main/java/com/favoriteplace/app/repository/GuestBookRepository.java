package com.favoriteplace.app.repository;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.community.GuestBook;
import com.favoriteplace.app.domain.community.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GuestBookRepository extends JpaRepository<GuestBook, Long> {
    List<GuestBook> findByCreatedAtBetweenOrderByLikeCountDesc(LocalDateTime start, LocalDateTime end);
    List<GuestBook> findByMemberOrderByCreatedAtDesc(Member member);

    Page<GuestBook> findAllByMemberIdOrderByCreatedAtDesc(Long id, Pageable pageable);

    Page<GuestBook> findAllByOrderByLikeCountDesc(Pageable pageable);

    Page<GuestBook> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Long countByMember(Member member);
}
