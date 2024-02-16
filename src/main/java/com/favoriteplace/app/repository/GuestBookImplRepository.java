package com.favoriteplace.app.repository;

import com.favoriteplace.app.domain.community.GuestBook;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GuestBookImplRepository {

    @PersistenceContext
    private final EntityManager em;

    public List<GuestBook> findAllByOrderByCreatedAtDesc(int page, int size){
        return em.createQuery(
            "select g from GuestBook g" +
                    " join fetch g.member m" +
                    " order by g.createdAt desc", GuestBook.class)
                .setFirstResult((page -1)*size)
                .setMaxResults(size)
                .getResultList();
    }

    public List<GuestBook> findAllByOrderByLikeCountDesc(int page, int size){
        return em.createQuery(
                "select g from GuestBook g" +
                        " join fetch g.member m" +
                        " order by g.likeCount desc" , GuestBook.class)
                .setFirstResult((page -1)*size)
                .setMaxResults(size)
                .getResultList();
    }

    public List<GuestBook> findAllByMemberIdOrderByCreatedAtDesc(Long memberId, int page, int size) {
        return em.createQuery(
                "select g from GuestBook g" +
                        " join fetch g.member m" +
                        " where g.member.id = :memberId" +
                        " order by g.createdAt desc", GuestBook.class)
                .setParameter("memberId", memberId)
                .setFirstResult((page-1)*size)
                .setMaxResults(size)
                .getResultList();
    }

    public GuestBook findOneById(Long guestBookId) {
        return em.createQuery(
                "select g from GuestBook g"+
                        " join fetch g.member m" +
                        " join fetch g.pilgrimage p" +
                        " left join fetch m.profileIcon pi" +
                        " left join fetch m.profileTitle pt" +
                        " left join fetch pi.defaultImage pii" +
                        " left join fetch pt.defaultImage pti" +
                        " left join fetch p.address pa" +
                        " left join fetch p.realImage pri" +
                        " left join fetch p.virtualImage pvi" +
                        " join fetch p.rally r" +
                        " where g.id = :guestBookId", GuestBook.class)
                .setParameter("guestBookId", guestBookId)
                .getSingleResult();
    }

    public List<GuestBook> searchByTitleUsingKeyword(String keyword, int page, int size) {
        return em.createQuery(
                "select g from GuestBook g"+
                        " join fetch g.member m"+
                        " where g.title like :keyword" +
                        " order by g.createdAt desc", GuestBook.class)
                .setParameter("keyword", "%"+keyword+"%")
                .setFirstResult((page-1)*size)
                .setMaxResults(size)
                .getResultList();
    }

    public List<GuestBook> searchByNicknameUsingKeyword(String keyword, int page, int size) {
        return em.createQuery(
                "select g from GuestBook g" +
                        " join fetch g.member m" +
                        " where m.nickname like :keyword" +
                        " order by g.createdAt desc", GuestBook.class)
                .setParameter("keyword", "%"+keyword+"%")
                .setFirstResult((page-1)*size)
                .setMaxResults(size)
                .getResultList();
    }

    public List<GuestBook> searchByContentUsingKeyword(String keyword, int page, int size) {
        return em.createQuery(
                "select g from GuestBook g" +
                        " join fetch g.member m" +
                        " where g.content like :keyword" +
                        " order by g.createdAt desc", GuestBook.class)
                .setParameter("keyword", "%"+keyword+"%")
                .setFirstResult((page-1)*size)
                .setMaxResults(size)
                .getResultList();
    }

    public List<GuestBook> findByCreatedAtBetweenOrderByLikeCountDesc(LocalDateTime startDateTime, LocalDateTime now, int size) {
        return em.createQuery(
                "select g from GuestBook g"+
                        " join fetch g.member m" +
                        " left join fetch m.profileTitle pt" +
                        " left join fetch m.profileIcon pi" +
                        " left join fetch pt.defaultImage pti" +
                        " left join fetch pi.defaultImage pii" +
                        " where g.createdAt between :startDateTime and :now" +
                        " order by g.likeCount desc", GuestBook.class)
                .setParameter("startDateTime", startDateTime)
                .setParameter("now", now)
                .setMaxResults(size)
                .getResultList();
    }
}
