package com.favoriteplace.app.community.service.searchStrategy;

import com.favoriteplace.app.community.domain.GuestBook;
import com.favoriteplace.app.community.repository.GuestBookImplRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SearchGuestBookByContent implements SearchStrategy<GuestBook> {
    private final GuestBookImplRepository guestBookImplRepository;

    @Override
    public List<GuestBook> search(String keyword, int page, int size) {
        return guestBookImplRepository.searchByContentUsingKeyword(keyword.trim(), page, size);
    }
}
