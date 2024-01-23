package com.favoriteplace.app.service.strategy;

import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.dto.community.PostResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SortStrategy {
    Page<Post> sort(Pageable pageable);
}
