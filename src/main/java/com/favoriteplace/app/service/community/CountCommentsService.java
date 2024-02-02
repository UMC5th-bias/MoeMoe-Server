package com.favoriteplace.app.service.community;

import com.favoriteplace.app.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CountCommentsService {
    private final CommentRepository commentRepository;

    Long countPostComments(Long postId){
        return commentRepository.countByPostId(postId) != null ? commentRepository.countByPostId(postId) : 0L;
    }

    Long countGuestBookComments(Long guestBookId){
        return commentRepository.countByGuestBookId(guestBookId) != null ? commentRepository.countByGuestBookId(guestBookId) : 0L;
    }
}
