package com.favoriteplace.app.repository;

import com.favoriteplace.app.domain.Image;
import com.favoriteplace.app.domain.community.GuestBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Image findFirstByGuestBook(GuestBook guestBook);
}
