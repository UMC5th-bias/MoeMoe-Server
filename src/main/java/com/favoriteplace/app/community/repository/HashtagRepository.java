package com.favoriteplace.app.community.repository;

import com.favoriteplace.app.community.domain.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashtagRepository extends JpaRepository<HashTag, Long> {
    List<HashTag> findAllByGuestBookId(Long guestBookId);

    void deleteByGuestBookId(Long guestbookId);
}
