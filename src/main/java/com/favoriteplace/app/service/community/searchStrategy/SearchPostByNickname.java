package com.favoriteplace.app.service.community.searchStrategy;

import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.repository.PostImplRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SearchPostByNickname implements SearchStrategy<Post>{
    private final PostImplRepository postImplRepository;

    @Override
    public List<Post> search(String keyword, int page, int size) {
        return postImplRepository.searchByNicknameUsingKeyword(keyword.trim(), page, size);
    }
}
