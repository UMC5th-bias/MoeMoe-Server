package com.favoriteplace.app.community.repository;

import com.favoriteplace.app.community.domain.Comment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentImplRepository {

    @PersistenceContext
    private final EntityManager em;

    public List<Comment> findMyPostComments(Long memberId, int page, int size) {
        return em.createQuery(
                "select c from Comment c" +
                        " join fetch c.post p" +
                        " join fetch p.member m" +
                        " where c.member.id = :memberId and c.guestBook.id is null and c.isDeleted != true" +
                        " order by c.createdAt desc", Comment.class)
                .setParameter("memberId", memberId)
                .setFirstResult((page-1)*size)
                .setMaxResults(size)
                .getResultList();
    }

    public List<Comment> findMyGuestBookComments(Long memberId, int page, int size) {
        return em.createQuery(
                "select c from Comment c" +
                        " join fetch c.guestBook g" +
                        " join fetch g.member m" +
                        " where c.member.id = :memberId and c.post.id is null and c.isDeleted != true" +
                        " order by c.createdAt desc", Comment.class)
                .setParameter("memberId", memberId)
                .setFirstResult((page-1)*size)
                .setMaxResults(size)
                .getResultList();
    }

    public List<Comment> findParentCommentsByPostId(Long postId, int page, int size) {
        return em.createQuery(
                        "select c from Comment c" +
                                " join fetch c.member m" +
                                " left join fetch m.profileIcon pi" +
                                " left join fetch m.profileTitle pt" +
                                " left join fetch pi.defaultImage pii" +
                                " left join fetch pt.defaultImage pti" +
                                " where c.post.id = :postId and c.parentComment = null" +
                                " order by c.createdAt asc", Comment.class)
                .setParameter("postId", postId)
                .setFirstResult((page-1)*size)
                .setMaxResults(size)
                .getResultList();
    }

    public List<Comment> findParentCommentByGuestBookId(Long guestbookId, int page, int size) {
        return em.createQuery(
                        "select c from Comment c" +
                                " join fetch c.member m" +
                                " left join fetch m.profileIcon pi" +
                                " left join fetch m.profileTitle pt" +
                                " left join fetch pi.defaultImage pii" +
                                " left join fetch pt.defaultImage pti" +
                                " where c.guestBook.id = :guestbookId and c.parentComment = null" +
                                " order by c.createdAt asc", Comment.class)
                .setParameter("guestbookId", guestbookId)
                .setFirstResult((page-1)*size)
                .setMaxResults(size)
                .getResultList();
    }

    public List<Comment> findSubCommentByCommentId(Long commentId){
        return em.createQuery(
                "select c from Comment c"+
                        " join fetch c.member m"+
                        " left join fetch c.referenceComment rc "+
                        " left join fetch m.profileIcon pi"+
                        " left join fetch m.profileTitle pt"+
                        " left join fetch pi.defaultImage pii" +
                        " left join fetch pt.defaultImage pti" +
                        " where c.parentComment.id = :commentId"+
                        " order by c.createdAt asc", Comment.class)
                .setParameter("commentId", commentId)
                .getResultList();
    }

}
