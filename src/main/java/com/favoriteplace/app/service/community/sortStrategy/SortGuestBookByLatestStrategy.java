package com.favoriteplace.app.service.community.sortStrategy;

import com.favoriteplace.app.domain.community.GuestBook;
import com.favoriteplace.app.repository.GuestBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SortGuestBookByLatestStrategy implements SortStrategy<GuestBook> {
    private final GuestBookRepository guestBookRepository;
    @Override
    public Page<GuestBook> sort(Pageable pageable) {
        return guestBookRepository.findAllByOrderByCreatedAtDesc(pageable);
    }
}
