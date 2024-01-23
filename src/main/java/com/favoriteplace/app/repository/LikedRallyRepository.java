package com.favoriteplace.app.repository;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.travel.LikedRally;
import com.favoriteplace.app.domain.travel.Rally;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikedRallyRepository extends JpaRepository<LikedRally, Long> {
    LikedRally findByRallyAndMember(Rally rally, Member member);
    List<LikedRally> findByMember(Member member);
}
