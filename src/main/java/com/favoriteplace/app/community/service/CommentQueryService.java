package com.favoriteplace.app.community.service;

import com.favoriteplace.app.community.converter.CommentConverter;
import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.community.domain.Comment;
import com.favoriteplace.app.community.controller.dto.comment.CommentParentResponseDto;
import com.favoriteplace.app.community.controller.dto.guestbook.GuestBookResponseDto;
import com.favoriteplace.app.community.controller.dto.PostResponseDto;
import com.favoriteplace.app.community.controller.dto.comment.CommentRootResponseDto;
import com.favoriteplace.app.community.repository.CommentImplRepository;
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
     */
    public CommentRootResponseDto getPostComments(Member member, int page, int size, Long postId) {
        List<Comment> commentPage = commentImplRepository.findParentCommentsByPostId(postId, page, size);
        return makeCommentDtoFromParentComment(commentPage, member, page);
    }

    /**
     * 커뷰니티 : 성지순례 인증글의 댓글 전부 보여주기
     *
     * @return 페이징 된 댓글 리스트
     */
    public CommentRootResponseDto getGuestBookComments(int page, int size, Member member, Long guestbookId) {
        // 부모 댓글 가져옴
        List<Comment> commentPage = commentImplRepository.findParentCommentByGuestBookId(guestbookId, page, size);
        return makeCommentDtoFromParentComment(commentPage, member, page);
    }


    /**
     * 자유게시판에서 내가 작성한 댓글을 불려오는 기능 (삭제한 댓글은 안보여줌)
     */
    public PostResponseDto.MyCommentDto getMyPostComments(Member member, int page, int size) {
        List<Comment> pageComment = commentImplRepository.findMyPostComments(member.getId(), page, size);
        List<PostResponseDto.MyComment> comments;
        if (pageComment.isEmpty()) {
            comments = Collections.emptyList();
        } else {
            comments = pageComment.stream()
                    .map(CommentConverter::toMyPostComment)
                    .toList();
        }
        return PostResponseDto.MyCommentDto.builder()
                .page((long) page)
                .size((long) comments.size())
                .comment(comments)
                .build();
    }

    /**
     * 사용자가 성지순례 인증글에서 작성한 댓글들을 모두 보여주는 함수 (삭제한 댓글은 안보여줌)
     */
    public GuestBookResponseDto.MyGuestBookCommentDto getMyGuestBookComments(Member member, int page, int size) {
        List<Comment> pageComment = commentImplRepository.findMyGuestBookComments(member.getId(), page, size);
        List<GuestBookResponseDto.MyGuestBookComment> comments;
        if (pageComment.isEmpty()) {
            comments = Collections.emptyList();
        } else {
            comments = pageComment.stream()
                    .map(CommentConverter::toMyGuestBookComment).toList();
        }
        return GuestBookResponseDto.MyGuestBookCommentDto.builder()
                .page((long) page)
                .size((long) comments.size())
                .comment(comments)
                .build();
    }


    /**
     * 부모 댓글을 사용해서 자식 댓글 매핑하고, DTO 생성
     */
    private CommentRootResponseDto makeCommentDtoFromParentComment(List<Comment> commentPage, Member member, int page) {
        // 자식 댓글 가져옴
        List<CommentParentResponseDto> comments;
        if (commentPage.isEmpty()) {
            comments = Collections.emptyList();
        } else {
            comments = commentPage.stream()
                    .map(comment -> CommentConverter.toComment(comment, member,
                            commentImplRepository.findSubCommentByCommentId(comment.getId())))
                    .toList();
        }
        // size 계산
        int pageSize = 0;
        for (CommentParentResponseDto parentComment : comments) {
            pageSize += (1 + parentComment.subComments().size());
        }
        return new CommentRootResponseDto((long) page, (long) pageSize, comments);
    }
}
