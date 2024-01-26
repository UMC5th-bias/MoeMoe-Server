package com.favoriteplace.app.service.sortStrategy;

import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SortPostByLatestStrategy implements SortStrategy<Post> {
    private final PostRepository postRepository;
    @Override
    public Page<Post> sort(Pageable pageable) {
        return postRepository.findAllByOrderByCreatedAtDesc(pageable);
    }
}
