package com.favoriteplace.app.community.repository;

import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.community.domain.GuestBook;
import com.favoriteplace.app.pilgrimage.domain.Pilgrimage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuestBookRepository extends JpaRepository<GuestBook, Long> {
    List<GuestBook> findByMemberOrderByCreatedAtDesc(Member member);

    Long countByMember(Member member);

    List<GuestBook> findByMemberAndPilgrimageOrderByCreatedAtDesc(Member member, Pilgrimage pilgrimage);
}
