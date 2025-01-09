package com.favoriteplace.app.repository;

import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.domain.community.GuestBook;
import com.favoriteplace.app.domain.travel.Pilgrimage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuestBookRepository extends JpaRepository<GuestBook, Long> {
    List<GuestBook> findByMemberOrderByCreatedAtDesc(Member member);

    Long countByMember(Member member);

    List<GuestBook> findByMemberAndPilgrimageOrderByCreatedAtDesc(Member member, Pilgrimage pilgrimage);
}
