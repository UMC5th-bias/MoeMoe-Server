package com.favoriteplace.app.repository;

import com.favoriteplace.app.domain.community.GuestBook;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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

}
