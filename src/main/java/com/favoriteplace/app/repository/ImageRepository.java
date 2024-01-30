package com.favoriteplace.app.repository;

import com.favoriteplace.app.domain.Image;
import com.favoriteplace.app.domain.community.GuestBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByPostId(Long postId);
    Image findFirstByGuestBook(GuestBook guestBook);
    List<Image> findAllByGuestBookId(Long GuestBookId);
    void deleteByPostId(long postId);
    void deleteByGuestBookId(Long guestbookId);
}
