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
                        " order by c.createdAt desc"
                        , Comment.class)
                .setParameter("memberId", memberId)
                .setFirstResult((page-1)*size)
                .setMaxResults(size)
                .getResultList();
    }
}
