package com.favoriteplace.app.service.sortStrategy;

import com.favoriteplace.app.domain.community.GuestBook;
import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.repository.GuestBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SortGuestBookByLikedStrategy implements SortStrategy<GuestBook> {
    private final GuestBookRepository guestBookRepository;

    @Override
    public Page<GuestBook> sort(Pageable pageable) {
        return guestBookRepository.findAllByOrderByLikeCountDesc(pageable);
    }
}
