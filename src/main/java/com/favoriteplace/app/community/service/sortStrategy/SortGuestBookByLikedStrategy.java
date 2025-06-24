package com.favoriteplace.app.community.service.sortStrategy;

import com.favoriteplace.app.community.domain.GuestBook;
import com.favoriteplace.app.community.repository.GuestBookImplRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SortGuestBookByLikedStrategy implements SortStrategy<GuestBook> {
    private final GuestBookImplRepository guestBookImplRepository;

    @Override
    public List<GuestBook> sort(int page, int size) {
        return guestBookImplRepository.findAllByOrderByLikeCountDesc(page, size);
    }
}
