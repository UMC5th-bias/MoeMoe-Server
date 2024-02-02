package com.favoriteplace.app.service.community.sortStrategy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SortStrategy<T> {
    Page<T> sort(Pageable pageable);
}
