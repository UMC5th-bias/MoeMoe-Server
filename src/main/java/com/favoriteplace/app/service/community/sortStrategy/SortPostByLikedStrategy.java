package com.favoriteplace.app.service.community.sortStrategy;

import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.repository.PostImplRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SortPostByLikedStrategy implements SortStrategy<Post>{
    private final PostImplRepository postImplRepository;
    @Override
    public List<Post> sort(int page, int size) {
        return postImplRepository.findAllByOrderByLikeCountDesc(page, size);
    }
}
