package com.favoriteplace.app.community.service;

import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.notification.domain.Notification;
import com.favoriteplace.app.community.domain.Comment;
import com.favoriteplace.app.community.domain.GuestBook;
import com.favoriteplace.app.community.domain.Post;
import com.favoriteplace.app.community.domain.enums.CommentType;
import com.favoriteplace.app.community.controller.dto.comment.CommentCreateRequestDto;
import com.favoriteplace.app.community.repository.CommentRepository;
import com.favoriteplace.app.community.repository.GuestBookRepository;
import com.favoriteplace.app.notification.repository.NotificationRepository;
import com.favoriteplace.app.community.repository.PostRepository;
import com.favoriteplace.app.notification.service.FCMNotificationService;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.favoriteplace.app.notification.converter.FcmConverter.*;
import static com.favoriteplace.app.notification.converter.NotificationConverter.*;

@Service
public class CommentCommandService {

    private final PostRepository postRepository;
    private final GuestBookRepository guestBookRepository;
    private final CommentRepository commentRepository;
    private final NotificationRepository notificationRepository;

    @Lazy
    @Autowired(required = false)
    private FCMNotificationService fcmNotificationService;

    @Value("${fcm.enabled:false}")
    private boolean fcmEnabled;

    public CommentCommandService(PostRepository postRepository,
                                 GuestBookRepository guestBookRepository,
                                 CommentRepository commentRepository,
                                 NotificationRepository notificationRepository) {
        this.postRepository = postRepository;
        this.guestBookRepository = guestBookRepository;
        this.commentRepository = commentRepository;
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public Long createPostComment(Member member, long postId, CommentCreateRequestDto dto) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RestApiException(ErrorCode.POST_NOT_FOUND));
        Comment newComment = setCommentRelation(member, dto);
        post.addComment(newComment);
        commentRepository.save(newComment);
        return newComment.getId();
    }

    @Transactional
    public Long createGuestBookComment(Member member, Long guestbookId, CommentCreateRequestDto dto) {
        GuestBook guestBook = guestBookRepository.findById(guestbookId)
                .orElseThrow(() -> new RestApiException(ErrorCode.GUESTBOOK_NOT_FOUND));
        Comment newComment = setCommentRelation(member, dto);
        guestBook.addComment(newComment);
        commentRepository.save(newComment);
        return newComment.getId();
    }

    @Transactional
    public void sendPostNotification(Long postId, Long commentId) {
        if (fcmNotificationService == null) return;

        Post post = postRepository.findById(postId).orElseThrow(() -> new RestApiException(ErrorCode.POST_NOT_FOUND));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RestApiException(ErrorCode.COMMENT_NOT_FOUND));

        fcmNotificationService.sendNotificationByToken(toPostWriter(post, comment));
        notificationRepository.save(toPostNewComment(post, comment));

        if (comment.getParentComment() != null) {
            Notification commentNotification;
            if (comment.getReferenceComment() == null) {
                fcmNotificationService.sendNotificationByToken(toParentCommentWriter(post, comment));
                commentNotification = toPostParentNewSubComment(post, comment);
            } else {
                fcmNotificationService.sendNotificationByToken(toReferCommentWriter(post, comment));
                commentNotification = toPostReferNewSubComment(post, comment);
            }
            notificationRepository.save(commentNotification);
        }
    }

    @Transactional
    public void sendGuestBookNotification(Long guestBookId, Long commentId) {
        if (fcmNotificationService == null) return;

        GuestBook guestBook = guestBookRepository.findById(guestBookId)
                .orElseThrow(() -> new RestApiException(ErrorCode.GUESTBOOK_NOT_FOUND));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RestApiException(ErrorCode.COMMENT_NOT_FOUND));

        fcmNotificationService.sendNotificationByToken(toGuestBookWriter(guestBook, comment));
        notificationRepository.save(toGuestBookNewComment(guestBook, comment));

        if (comment.getParentComment() != null) {
            Notification commentNotification;
            if (comment.getReferenceComment() == null) {
                fcmNotificationService.sendNotificationByToken(toParentCommentWriter(guestBook, comment));
                commentNotification = toGuestBookParentNewSubComment(guestBook, comment);
            } else {
                fcmNotificationService.sendNotificationByToken(toReferCommentWriter(guestBook, comment));
                commentNotification = toGuestBookReferNewSubComment(guestBook, comment);
            }
            notificationRepository.save(commentNotification);
        }
    }

    @Transactional
    public void modifyComment(Member member, long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RestApiException(ErrorCode.COMMENT_NOT_FOUND));
        checkAuthOfComment(member, comment);
        checkIsDeleteOfComment(comment);
        Optional.ofNullable(content).ifPresent(comment::modifyContent);
    }

    @Transactional
    public void deleteComment(Member member, long commendId) {
        Comment comment = commentRepository.findById(commendId)
                .orElseThrow(() -> new RestApiException(ErrorCode.COMMENT_NOT_FOUND));
        checkAuthOfComment(member, comment);
        checkIsDeleteOfComment(comment);

        if (comment.getCommentType() == CommentType.PARENT_COMMENT) {
            if (commentRepository.existsByParentComment(comment)) {
                comment.softDeleteComment();
            } else {
                commentRepository.delete(comment);
            }
        } else {
            if (commentRepository.existsByReferenceComment(comment)) {
                comment.softDeleteComment();
            } else {
                commentRepository.delete(comment);
                Comment lastDeleteComment = hardDeleteReferenceComment(comment);
                hardDeleteParentComment(lastDeleteComment);
            }
        }
    }

    private Comment hardDeleteReferenceComment(Comment comment) {
        Comment referenceComment = comment.getReferenceComment();
        if (referenceComment != null && referenceComment.isDeleted() &&
                !commentRepository.existsByReferenceComment(referenceComment)) {
            commentRepository.delete(referenceComment);
            return hardDeleteReferenceComment(referenceComment);
        } else {
            return comment;
        }
    }

    private void hardDeleteParentComment(Comment comment) {
        Comment parentComment = comment.getParentComment();
        if (parentComment != null && parentComment.isDeleted() &&
                !commentRepository.existsByParentComment(parentComment)) {
            commentRepository.delete(parentComment);
        }
    }

    private void checkAuthOfComment(Member member, Comment comment) {
        if (!member.getId().equals(comment.getMember().getId())) {
            throw new RestApiException(ErrorCode.USER_NOT_AUTHOR);
        }
    }

    private void checkIsDeleteOfComment(Comment comment) {
        if (comment.isDeleted()) {
            throw new RestApiException(ErrorCode.COMMENT_ALREADY_DELETED);
        }
    }

    private Comment setCommentRelation(Member member, CommentCreateRequestDto dto) {
        Comment newComment = Comment.builder()
                .member(member)
                .commentType(CommentType.PARENT_COMMENT)
                .content(dto.content())
                .build();

        if (dto.parentCommentId() != null) {
            Comment parentComment = commentRepository.findById(dto.parentCommentId())
                    .orElseThrow(() -> new RestApiException(ErrorCode.COMMENT_NOT_FOUND));
            if (parentComment.getCommentType() != CommentType.PARENT_COMMENT) {
                throw new RestApiException(ErrorCode.COMMENT_NOT_PARENT);
            }
            newComment.setCommentType(CommentType.CHILD_COMMENT);
            newComment.addParentComment(parentComment);

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
