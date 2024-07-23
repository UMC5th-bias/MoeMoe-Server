package com.favoriteplace.app.service.community;

import com.favoriteplace.app.converter.CommentConverter;
import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.community.Comment;
import com.favoriteplace.app.dto.community.CommentResponseDto;
import com.favoriteplace.app.dto.community.GuestBookResponseDto;
import com.favoriteplace.app.dto.community.PostResponseDto;
import com.favoriteplace.app.repository.CommentImplRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentQueryService {
    private final CommentImplRepository commentImplRepository;

    /**
     * 특정 자유게시글에 작성된 댓글들을 페이징해서 보여주는 함수
     * @param page
     * @param size
     * @param postId
     * @return
     */
    public List<CommentResponseDto.Comment> getPostComments(Member member, int page, int size, Long postId) {
        List<Comment> commentPage = commentImplRepository.findParentCommentsByPostId(postId, page, size);
        if(commentPage.isEmpty()){return Collections.emptyList();}
        return commentPage.stream()
                .map(comment -> CommentConverter.toComment(comment, member, commentImplRepository.findSubCommentByCommentId(comment.getId())))
                .toList();
    }

    /**
     * 커뷰니티 : 성지순례 인증글의 댓글 전부 보여주기
     * @param page
     * @param size
     * @param member
     * @param guestbookId
     * @return 페이징 된 댓글 리스트
     */
    public List<CommentResponseDto.Comment> getGuestBookComments(int page, int size, Member member, Long guestbookId) {
        // 부모 댓글 가져옴
        List<Comment> commentPage = commentImplRepository.findParentCommentByGuestBookId(guestbookId, page, size);
        if(commentPage.isEmpty()){return Collections.emptyList();}
        return commentPage.stream()
                .map(comment -> CommentConverter.toComment(comment, member, commentImplRepository.findSubCommentByCommentId(comment.getId())))
                .toList();
    }

    /**
     * 자유게시판에서 내가 작성한 댓글을 불려오는 기능
     * @param page
     * @param size
     * @return
     */
    public List<PostResponseDto.MyComment> getMyPostComments(Member member, int page, int size) {
        List<Comment> pageComment = commentImplRepository.findAllByMemberIdAndPostIsNotNullAndGuestBookIsNullOrderByCreatedAtDesc(member.getId(), page, size);
        if(pageComment.isEmpty()){return Collections.emptyList();}
        return pageComment.stream()
                .map(CommentConverter::toMyPostComment)
                .toList();
    }

    /**
     * 사용자가 성지순례 인증글에서 작성한 댓글들을 모두 보여주는 함수
     * @param page
     * @param size
     * @return
     */
    public List<GuestBookResponseDto.MyGuestBookComment> getMyGuestBookComments(Member member, int page, int size) {
        List<Comment> pageComment = commentImplRepository.findAllByMemberIdAndPostIsNullAndGuestBookIsNotNullOrderByCreatedAtDesc(member.getId(), page, size);
        if(pageComment.isEmpty()){return Collections.emptyList();}
        return pageComment.stream()
                .map(CommentConverter::toMyGuestBookComment).toList();
    }
    
}
