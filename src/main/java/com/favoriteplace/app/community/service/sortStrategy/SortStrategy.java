package com.favoriteplace.app.community.service.sortStrategy;

import java.util.List;

public interface SortStrategy<T> {
    List<T> sort(int page, int size);
}
