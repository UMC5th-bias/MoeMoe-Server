package com.favoriteplace.app.repository;

import com.favoriteplace.app.domain.community.Comment;
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

    public List<Comment> findAllByMemberIdAndPostIsNotNullAndGuestBookIsNullOrderByCreatedAtDesc(Long memberId, int page, int size) {
        return em.createQuery(
                "select c from Comment c" +
                        " join fetch c.post p" +
                        " join fetch p.member m" +
                        " where c.member.id = :memberId and c.guestBook.id is null" +
                        " order by c.createdAt desc", Comment.class)
                .setParameter("memberId", memberId)
                .setFirstResult((page-1)*size)
                .setMaxResults(size)
                .getResultList();
    }

    public List<Comment> findAllByMemberIdAndPostIsNullAndGuestBookIsNotNullOrderByCreatedAtDesc(Long memberId, int page, int size) {
        return em.createQuery(
                "select c from Comment c" +
                        " join fetch c.guestBook g" +
                        " join fetch g.member m" +
                        " where c.member.id = :memberId and c.post.id is null" +
                        " order by c.createdAt desc", Comment.class)
                .setParameter("memberId", memberId)
                .setFirstResult((page-1)*size)
                .setMaxResults(size)
                .getResultList();
    }

    public List<Comment> findAllByPostIdOrderByCreatedAtAsc(Long postId, int page, int size) {
        return em.createQuery(
                "select c from Comment c" +
                        " join fetch c.member m" +
                        " left join fetch m.profileIcon pi" +
                        " left join fetch m.profileTitle pt" +
                        " left join fetch pi.image pii" +
                        " left join fetch pt.image pti" +
                        " where c.post.id = :postId" +
                        " order by c.createdAt asc", Comment.class)
                .setParameter("postId", postId)
                .setFirstResult((page-1)*size)
                .setMaxResults(size)
                .getResultList();
    }

    public List<Comment> findAllByGuestBookIdOrderByCreatedAtAsc(Long guestbookId, int page, int size) {
        return em.createQuery(
                "select c from Comment c" +
                        " join fetch c.member m" +
                        " left join fetch m.profileIcon pi" +
                        " left join fetch m.profileTitle pt" +
                        " left join fetch pi.image pii" +
                        " left join fetch pt.image pti" +
                        " where c.guestBook.id = :guestbookId" +
                        " order by c.createdAt asc", Comment.class)
                .setParameter("guestbookId", guestbookId)
                .setFirstResult((page-1)*size)
                .setMaxResults(size)
                .getResultList();
    }
}
