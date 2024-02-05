package com.favoriteplace.app.service.community.searchStrategy;

import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SearchPostByContent implements SearchStrategy<Post> {
    private final PostRepository postRepository;
    @Override
    public Page<Post> search(String keyword, Pageable pageable) {
        return postRepository.searchPostByContentUsingKeyword(keyword.trim(), pageable);
    }
}
