package com.favoriteplace.app.service.community;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.Notification;
import com.favoriteplace.app.domain.community.Comment;
import com.favoriteplace.app.domain.community.GuestBook;
import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.domain.enums.CommentType;
import com.favoriteplace.app.dto.community.CommentCreateRequestDto;
import com.favoriteplace.app.repository.CommentRepository;
import com.favoriteplace.app.repository.GuestBookRepository;
import com.favoriteplace.app.repository.NotificationRepository;
import com.favoriteplace.app.repository.PostRepository;
import com.favoriteplace.app.service.fcm.FCMNotificationService;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.favoriteplace.app.converter.FcmConverter.*;
import static com.favoriteplace.app.converter.NotificationConverter.*;

@Service
@RequiredArgsConstructor
public class CommentCommandService {
    private final PostRepository postRepository;
    private final GuestBookRepository guestBookRepository;
    private final CommentRepository commentRepository;
    private final NotificationRepository notificationRepository;
    private final FCMNotificationService fcmNotificationService;

    /**
     * 자유게시글 새로운 댓글 작성
     */
    @Transactional
    public Long createPostComment(Member member, long postId, CommentCreateRequestDto dto) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RestApiException(ErrorCode.POST_NOT_FOUND));
        Comment newComment = setCommentRelation(member, dto);
        post.addComment(newComment);
        commentRepository.save(newComment);
        return newComment.getId();
    }

    /**
     * 성지순례 인증글에 댓글 추가
     */
    @Transactional
    public Long createGuestBookComment(Member member, Long guestbookId, CommentCreateRequestDto dto) {
        GuestBook guestBook = guestBookRepository.findById(guestbookId)
                .orElseThrow(() -> new RestApiException(ErrorCode.GUESTBOOK_NOT_FOUND));
        Comment newComment = setCommentRelation(member, dto);
        guestBook.addComment(newComment);
        commentRepository.save(newComment);
        return newComment.getId();
    }

    /**
     * 자유 게시판 관련 알림 전송
     */
    @Transactional
    public void sendPostNotification(Long postId, Long commentId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RestApiException(ErrorCode.POST_NOT_FOUND));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RestApiException(ErrorCode.COMMENT_NOT_FOUND));

        // 게시글 작성자에게 전송
        fcmNotificationService.sendNotificationByToken(toPostWriter(post, comment));
        Notification postNotification = toPostNewComment(post, comment);
        notificationRepository.save(postNotification);

        // 댓글에 언급된 사람에게 전송
        if (comment.getParentComment() != null) { // 부모 댓글에게 전송
            Notification commentNotification;
            if (comment.getReferenceComment() == null) {
                fcmNotificationService.sendNotificationByToken(toParentCommentWriter(post, comment));
                commentNotification = toPostParentNewSubComment(post, comment);
            } else { // reference 댓글에게 전송
                fcmNotificationService.sendNotificationByToken(toReferCommentWriter(post, comment));
                commentNotification = toPostReferNewSubComment(post, comment);
            }
            notificationRepository.save(commentNotification);
        }
    }

    /**
     * 성지 순례 인증글 관련 알림 전송
     */
    @Transactional
    public void sendGuestBookNotification(Long guestBookId, Long commentId) {
        GuestBook guestBook = guestBookRepository.findById(guestBookId)
                .orElseThrow(() -> new RestApiException(ErrorCode.GUESTBOOK_NOT_FOUND));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RestApiException(ErrorCode.COMMENT_NOT_FOUND));

        // 게시글 작성자에게 전송
        fcmNotificationService.sendNotificationByToken(toGuestBookWriter(guestBook, comment));
        Notification postNotification = toGuestBookNewComment(guestBook, comment);
        notificationRepository.save(postNotification);

        // 댓글에 언급된 사람에게 전송
        if (comment.getParentComment() != null) {
            Notification commentNotification;
            if (comment.getReferenceComment() == null) {  // 부모 댓글에게 전송
                fcmNotificationService.sendNotificationByToken(toParentCommentWriter(guestBook, comment));
                commentNotification = toGuestBookParentNewSubComment(guestBook, comment);
            } else { // reference 댓글에게 전송
                fcmNotificationService.sendNotificationByToken(toReferCommentWriter(guestBook, comment));
                commentNotification = toGuestBookReferNewSubComment(guestBook, comment);
            }
            notificationRepository.save(commentNotification);
        }
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public void modifyComment(Member member, long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RestApiException(ErrorCode.COMMENT_NOT_FOUND));
        checkAuthOfComment(member, comment);
        checkIsDeleteOfComment(comment);
        Optional.ofNullable(content).ifPresent(comment::modifyContent);
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public void deleteComment(Member member, long commendId) {
        Comment comment = commentRepository.findById(commendId)
                .orElseThrow(() -> new RestApiException(ErrorCode.COMMENT_NOT_FOUND));
        checkAuthOfComment(member, comment);
        checkIsDeleteOfComment(comment);
        // 최상위 댓글
        if (comment.getCommentType() == CommentType.PARENT_COMMENT) {
            // 대댓글이 있는 경우 - soft delete
            if (commentRepository.existsByParentComment(comment)) {
                comment.softDeleteComment();
            }
            // 대댓글이 없는 경우 - hard delete
            else {
                commentRepository.delete(comment);
            }

        } else {  // 대댓글
            // 나를 참조하는 댓글이 있는 경우 - soft delete
            if (commentRepository.existsByReferenceComment(comment)) {
                comment.softDeleteComment();
            }
            // 나를 참조하는 댓글이 없는 경우
            else {
                commentRepository.delete(comment);
                // 내가 참조하는 댓글이 soft delete -> hard delete 가능한 경우
                Comment lastDeleteComment = hardDeleteReferenceComment(comment);
                // 가장 마지막으로 삭제된 대댓글의 최상위 댓글이 soft delete -> hard delete 가능한 경우
                hardDeleteParentComment(lastDeleteComment);
            }
        }
    }

    /**
     * 내가 참조하는 댓글이 soft delete -> hard delete 가능한 경우
     */
    private Comment hardDeleteReferenceComment(Comment comment) {
        Comment referenceComment = comment.getReferenceComment();
        if (referenceComment != null && referenceComment.getIsDeleted() && !commentRepository.existsByReferenceComment(
                referenceComment)) {
            commentRepository.delete(referenceComment);
            return hardDeleteReferenceComment(referenceComment);
        } else {
            return comment;
        }
    }

    /**
     * 최상위 댓글 soft delete -> hard delete 가능한 경우
     *
     * @param comment
     */
    private void hardDeleteParentComment(Comment comment) {
        Comment parentComment = comment.getParentComment();
        if (parentComment.getIsDeleted() && !commentRepository.existsByParentComment(parentComment)) {
            commentRepository.delete(parentComment);
        }
    }

    /**
     * 댓글의 작성자가 맞는지 확인하는 함수 (만약 작성자가 아니라면 에러 출력)
     */
    private void checkAuthOfComment(Member member, Comment comment) {
        if (!member.getId().equals(comment.getMember().getId())) {
            throw new RestApiException(ErrorCode.USER_NOT_AUTHOR);
        }
    }

    /**
     * soft delete로 이미 삭제된 댓글인지 확인하는 함수 (삭제된 댓글이면 에러 출력)
     */
    private void checkIsDeleteOfComment(Comment comment) {
        if (comment.getIsDeleted()) {
            throw new RestApiException(ErrorCode.COMMENT_ALREADY_DELETED);
        }
    }

    /**
     * 댓글 연관관계 setting
     */
    private Comment setCommentRelation(Member member, CommentCreateRequestDto dto) {
        Comment newComment = Comment.builder()
                .member(member)
                .commentType(CommentType.PARENT_COMMENT)
                .content(dto.content())
                .build();
        // 대댓글
        if (dto.parentCommentId() != null) {
            Comment parentComment = commentRepository.findById(dto.parentCommentId())
                    .orElseThrow(() -> new RestApiException(ErrorCode.COMMENT_NOT_FOUND));
            if (parentComment.getCommentType() != CommentType.PARENT_COMMENT) {
                throw new RestApiException(ErrorCode.COMMENT_NOT_PARENT);
            }
            newComment.setCommentType(CommentType.CHILD_COMMENT);
            newComment.addParentComment(parentComment);
            // 다른 대댓글 참조 O
            if (dto.referenceCommentId() != null) {
                Comment referenceComment = commentRepository.findById(dto.referenceCommentId())
                        .orElseThrow(() -> new RestApiException(ErrorCode.COMMENT_NOT_FOUND));
                if (referenceComment.getCommentType() != CommentType.CHILD_COMMENT) {
                    throw new RestApiException(ErrorCode.COMMENT_NOT_CHILD);
                }
                newComment.setReferenceComment(referenceComment);
            }
        }
        return newComment;
    }
}
