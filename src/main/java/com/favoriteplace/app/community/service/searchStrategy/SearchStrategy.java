package com.favoriteplace.app.community.service.searchStrategy;

import java.util.List;

public interface SearchStrategy<T> {
    List<T> search(String keyword, int page, int size);
}
