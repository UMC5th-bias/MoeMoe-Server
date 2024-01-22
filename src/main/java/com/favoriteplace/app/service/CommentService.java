package com.favoriteplace.app.service;

import com.favoriteplace.app.converter.PostConverter;
import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.community.Comment;
import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.dto.community.PostResponseDto;
import com.favoriteplace.app.repository.CommentRepository;
import com.favoriteplace.app.repository.PostRepository;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.util.DateTimeFormatUtils;
import com.favoriteplace.global.util.SecurityUtil;
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
    private final SecurityUtil securityUtil;

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

    public List<PostResponseDto.MyComment> getMyComments(int page, int size) {
        Member member = securityUtil.getUser();
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> pageComment = commentRepository.findAllByMemberIdOrderByCreatedAtDesc(member.getId(), pageable);
        if(pageComment.isEmpty()){
            return Collections.emptyList();
        }
        List<PostResponseDto.MyComment> myComments = new ArrayList<>();
        for(Comment comment: pageComment.getContent()){
            Post post = comment.getPost();
            Long comments = commentRepository.countByPostId(post.getId()) != null ? commentRepository.countByPostId(post.getId()) : 0L;
            myComments.add(
                    PostResponseDto.MyComment.builder()
                            .id(comment.getId())
                            .content(comment.getContent())
                            .passedTime(DateTimeFormatUtils.getPassDateTime(comment.getCreatedAt()))
                            .post(PostConverter.myPostResponseConverter(post, member, comments))
                            .build()
            );
        }
        return myComments;
    }
}
