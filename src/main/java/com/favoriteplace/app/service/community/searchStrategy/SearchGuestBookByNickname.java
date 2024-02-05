package com.favoriteplace.app.service.community.searchStrategy;

import com.favoriteplace.app.domain.community.GuestBook;
import com.favoriteplace.app.dto.community.GuestBookResponseDto;
import com.favoriteplace.app.repository.GuestBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SearchGuestBookByNickname implements SearchStrategy<GuestBook> {
    private final GuestBookRepository guestBookRepository;

    @Override
    public Page<GuestBook> search(Pageable pageable) {
        return null;
    }
}
