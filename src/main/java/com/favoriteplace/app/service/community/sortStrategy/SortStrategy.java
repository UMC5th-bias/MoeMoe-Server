package com.favoriteplace.app.service.community.sortStrategy;

import java.util.List;

public interface SortStrategy<T> {
    List<T> sort(int page, int size);
}
