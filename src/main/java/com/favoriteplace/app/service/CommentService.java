package com.favoriteplace.app.service;

import com.favoriteplace.app.domain.community.Comment;
import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.dto.community.PostResponseDto;
import com.favoriteplace.app.repository.CommentRepository;
import com.favoriteplace.app.repository.PostRepository;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.util.DateTimeFormatUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberService memberService;

    public List<PostResponseDto.PostComment> getPostComments
            (int page, int size, Long postId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> commentPage = commentRepository.findAllByPostIdOrderByCreatedAtAsc(postId, pageable);
        if(commentPage.isEmpty()){
            return Collections.emptyList();
        }
        List<PostResponseDto.PostComment> comments = new ArrayList<>();
        for(Comment comment:commentPage.getContent()){
            comments.add(PostResponseDto.PostComment.builder()
                            .userInfo(memberService.getUserInfoByPostId(postId))
                            .id(comment.getId())
                            .content(comment.getContent())
                            .passedTime(DateTimeFormatUtils.getPassDateTime(comment.getCreatedAt()))
                            .isWrite(isWriter(postId, comment)).build()
            );
        }
        return comments;
    }

    private Boolean isWriter(Long postId, Comment comment){
        Optional<Post> optionalPost = postRepository.findById(postId);
        if(optionalPost.isEmpty()){
            throw new RestApiException(ErrorCode.POST_NOT_FOUND);
        }
        return optionalPost.get().getMember().getId().equals(comment.getMember().getId());
    }
}
