package com.favoriteplace.app.service.community.searchStrategy;

import com.favoriteplace.app.domain.community.GuestBook;
import com.favoriteplace.app.repository.GuestBookImplRepository;
import com.favoriteplace.app.repository.GuestBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SearchGuestBookByTitle implements SearchStrategy<GuestBook> {
    private final GuestBookImplRepository guestBookImplRepository;

    @Override
    public List<GuestBook> search(String keyword, int page, int size) {
        return guestBookImplRepository.searchByTitleUsingKeyword(keyword.trim(), page, size);
    }
}
