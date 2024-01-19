package com.favoriteplace.app.repository;

import com.favoriteplace.app.domain.community.GuestBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GuestBookRepository extends JpaRepository<GuestBook, Long> {
    List<GuestBook> findByCreatedAtBetweenOrderByLikeCountDesc(LocalDateTime start, LocalDateTime end);

}
