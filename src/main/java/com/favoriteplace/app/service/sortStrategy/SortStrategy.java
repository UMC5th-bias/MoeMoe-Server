package com.favoriteplace.app.service.sortStrategy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SortStrategy<T> {
    Page<T> sort(Pageable pageable);
}
