package com.favoriteplace.app.service.community.searchStrategy;

import com.favoriteplace.app.domain.community.GuestBook;
import com.favoriteplace.app.repository.GuestBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SearchGuestBookByContent implements SearchStrategy<GuestBook> {
    private final GuestBookRepository guestBookRepository;
    @Override
    public Page<GuestBook> search(String keyword, Pageable pageable) {
        return guestBookRepository.searchByContentUsingKeyword(keyword.trim(), pageable);
    }
}
