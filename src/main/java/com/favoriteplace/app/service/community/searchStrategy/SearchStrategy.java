package com.favoriteplace.app.service.community.searchStrategy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchStrategy<T> {
    Page<T> search(String keyword, Pageable pageable);
}
