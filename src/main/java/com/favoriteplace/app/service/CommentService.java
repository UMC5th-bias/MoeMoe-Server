package com.favoriteplace.app.service;

import com.favoriteplace.app.converter.CommentConverter;
import com.favoriteplace.app.converter.PostConverter;
import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.community.Comment;
import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.dto.community.CommentResponseDto;
import com.favoriteplace.app.dto.community.GuestBookResponseDto;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CountComments countComments;
    private final SecurityUtil securityUtil;

    /**
     * 특정 자유게시글에 작성된 댓글들을 페이징해서 보여주는 함수
     * @param page
     * @param size
     * @param postId
     * @return
     */
    @Transactional
    public Page<CommentResponseDto.PostComment> getPostComments(Member member, int page, int size, Long postId) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<Comment> commentPage = commentRepository.findAllByPostIdOrderByCreatedAtAsc(postId, pageable);
        if(commentPage.isEmpty()){return Page.empty();}
        return commentPage.map(comment -> CommentConverter.toPostComment(comment, isCommentWriter(member, comment)));
    }

    /**
     * 커뷰니티 : 성지순례 인증글의 댓글 전부 보여주기 (페이징 적용)
     * @param page
     * @param size
     * @param member
     * @param guestbookId
     * @return 페이징 된 댓글 리스트
     */
    @Transactional
    public Page<CommentResponseDto.PostComment> getGuestBookComments(int page, int size, Member member, Long guestbookId) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<Comment> commentPage = commentRepository.findAllByGuestBookIdOrderByCreatedAtAsc(guestbookId, pageable);
        if(commentPage.isEmpty()){return Page.empty();}
        return commentPage.map(comment -> CommentConverter.toPostComment(comment, isCommentWriter(member, comment)));
    }

    /**
     * 자유게시판에서 내가 작성한 댓글을 불려오는 기능
     * @param page
     * @param size
     * @return
     */
    @Transactional
    public Page<PostResponseDto.MyComment> getMyPostComments(Member member, int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<Comment> pageComment = commentRepository.findAllByMemberIdAndPostIsNotNullAndGuestBookIsNullOrderByCreatedAtDesc(member.getId(), pageable);
        if(pageComment.isEmpty()){return Page.empty();}
        return pageComment.map(comment -> CommentConverter.toMyGuestBookComment(comment, member, comment.getPost(), countComments.countPostComments(comment.getPost().getId())));
    }

    /**
     * 사용자가 성지순례 인증글에서 작성한 댓글들을 모두 보여주는 함수
     * @param page
     * @param size
     * @return
     */
    @Transactional
    public Page<GuestBookResponseDto.MyGuestBookComment> getMyGuestBookComments(int page, int size) {
        Member member = securityUtil.getUser();
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Comment> pageComment = commentRepository.findAllByMemberIdAndPostIsNullAndGuestBookIsNotNullOrderByCreatedAtDesc(member.getId(), pageable);
        if(pageComment.isEmpty()){return Page.empty();}
        return pageComment.map(comment -> CommentConverter.toMyGuestBookComment(comment, countComments.countGuestBookComments(comment.getGuestBook().getId())));
    }

    /**
     * 사용자(앱을 사용하는 유저)가 댓글 작성자가 맞는지 확인하는 함수
     * @param member
     * @param comment
     * @return 댓글 작성자가 맞으면 true, 아니면 false
     */
    private Boolean isCommentWriter(Member member, Comment comment){
        if(member == null){return false;}
        return member.getId().equals(comment.getMember().getId());
    }

    @Transactional
    public void createComment(long postId, String content) {
        Member member = securityUtil.getUser();
        Comment comment = Comment.builder()
                .member(member)
                .post(postRepository.findById(postId).orElseThrow(() -> new RestApiException(ErrorCode.POST_NOT_FOUND)))
                .content(content)
                .build();
        commentRepository.save(comment);
    }
}
