package com.favoriteplace.app.service.community;

import com.favoriteplace.app.converter.CommentConverter;
import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.community.Comment;
import com.favoriteplace.app.dto.community.CommentResponseDto;
import com.favoriteplace.app.dto.community.GuestBookResponseDto;
import com.favoriteplace.app.dto.community.PostResponseDto;
import com.favoriteplace.app.repository.CommentRepository;
import com.favoriteplace.app.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentQueryService {
    private final CommentRepository commentRepository;
    private final CountCommentsService countCommentsService;

    /**
     * 특정 자유게시글에 작성된 댓글들을 페이징해서 보여주는 함수
     * @param page
     * @param size
     * @param postId
     * @return
     */
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
    public Page<PostResponseDto.MyComment> getMyPostComments(Member member, int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<Comment> pageComment = commentRepository.findAllByMemberIdAndPostIsNotNullAndGuestBookIsNullOrderByCreatedAtDesc(member.getId(), pageable);
        if(pageComment.isEmpty()){return Page.empty();}
        return pageComment.map(comment -> CommentConverter.toMyGuestBookComment(comment, member, comment.getPost(), countCommentsService.countPostComments(comment.getPost().getId())));
    }

    /**
     * 사용자가 성지순례 인증글에서 작성한 댓글들을 모두 보여주는 함수
     * @param page
     * @param size
     * @return
     */
    public Page<GuestBookResponseDto.MyGuestBookComment> getMyGuestBookComments(Member member, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Comment> pageComment = commentRepository.findAllByMemberIdAndPostIsNullAndGuestBookIsNotNullOrderByCreatedAtDesc(member.getId(), pageable);
        if(pageComment.isEmpty()){return Page.empty();}
        return pageComment.map(comment -> CommentConverter.toMyGuestBookComment(comment, countCommentsService.countGuestBookComments(comment.getGuestBook().getId())));
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

}
