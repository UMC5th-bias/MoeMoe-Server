package com.favoriteplace.app.repository;

import com.favoriteplace.app.domain.community.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashtagRepository extends JpaRepository<HashTag, Long> {
    List<HashTag> findAllByGuestBookId(Long guestBookId);

    void deleteByGuestBookId(Long guestbookId);
}
