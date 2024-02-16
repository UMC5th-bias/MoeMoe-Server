package com.favoriteplace.app.service.community.searchStrategy;

import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.repository.PostImplRepository;
import com.favoriteplace.app.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
