package com.favoriteplace.app.service.strategy;

import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SortByLatestStrategy implements SortStrategy {
    private final PostRepository postRepository;
    @Override
    public Page<Post> sort(Pageable pageable) {
        return postRepository.findAllByOrderByCreatedAtDesc(pageable);
    }
}
