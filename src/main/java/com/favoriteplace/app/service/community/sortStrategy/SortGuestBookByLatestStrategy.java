package com.favoriteplace.app.service.community.sortStrategy;

import com.favoriteplace.app.domain.community.GuestBook;
import com.favoriteplace.app.repository.GuestBookImplRepository;
import com.favoriteplace.app.repository.GuestBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SortGuestBookByLatestStrategy implements SortStrategy<GuestBook> {
    private final GuestBookImplRepository guestBookImplRepository;
    @Override
    public List<GuestBook> sort(int page, int size) {
        return guestBookImplRepository.findAllByOrderByCreatedAtDesc(page, size);
    }
}
