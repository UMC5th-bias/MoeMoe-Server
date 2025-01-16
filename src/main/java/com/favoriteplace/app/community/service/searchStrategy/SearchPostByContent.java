package com.favoriteplace.app.community.service.searchStrategy;

import com.favoriteplace.app.community.domain.Post;
import com.favoriteplace.app.community.repository.PostImplRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SearchPostByContent implements SearchStrategy<Post> {
    private final PostImplRepository postImplRepository;

    @Override
    public List<Post> search(String keyword, int page, int size) {
        return postImplRepository.searchPostByContentUsingKeyword(keyword.trim(), page, size);
    }
}
