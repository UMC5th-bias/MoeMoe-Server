package com.favoriteplace.app.community.service.sortStrategy;

import com.favoriteplace.app.community.domain.Post;
import com.favoriteplace.app.community.repository.PostImplRepository;
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
