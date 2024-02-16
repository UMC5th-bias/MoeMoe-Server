package com.favoriteplace.app.service.community.searchStrategy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SearchStrategy<T> {
    List<T> search(String keyword, int page, int size);
}
