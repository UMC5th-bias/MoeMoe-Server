package com.favoriteplace.app.repository;

import com.favoriteplace.app.domain.community.Post;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostImplRepository {

    @PersistenceContext
    private final EntityManager em;

    public List<Post> findAllByOrderByCreatedAtDesc(int page, int size){
        return em.createQuery(
                "select p from Post p" +
                    " join fetch p.member m" +
                    " order by p.createdAt desc", Post.class)
                .setFirstResult((page-1)*size)
                .setMaxResults(size)
                .getResultList();
    }

    public List<Post> findAllByOrderByLikeCountDesc(int page, int size){
        return em.createQuery(
                "select p from Post p" +
                        " join fetch p.member m" +
                        " order by p.likeCount desc", Post.class)
                .setFirstResult((page-1)*size)
                .setMaxResults(size)
                .getResultList();
    }

    public List<Post> findAllByMemberIdOrderByCreatedAtDesc(Long memberId, int page, int size){
        return em.createQuery(
                "select p from Post p"+
                        " join fetch p.member m" +
                        " where m.id = :memberId" +
                        " order by p.createdAt desc", Post.class)
                .setParameter("memberId", memberId)
                .setFirstResult((page-1)*size)
                .setMaxResults(size)
                .getResultList();
    }


    public Post findOneById(Long postId) {
        return em.createQuery(
                "select p from Post p" +
                        " join fetch p.member m" +
                        " left join fetch m.profileIcon pi" +
                        " left join fetch m.profileTitle pt" +
                        " where p.id = :postId", Post.class)
                .setParameter("postId", postId)
                .getSingleResult();
    }
}
