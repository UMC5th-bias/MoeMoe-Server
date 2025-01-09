package com.favoriteplace.app.repository;

import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.domain.travel.LikedRally;
import com.favoriteplace.app.domain.travel.Rally;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LikedRallyRepository extends JpaRepository<LikedRally, Long> {
    LikedRally findByRallyAndMember(Rally rally, Member member);
    List<LikedRally> findByMember(Member member);
    @Query("SELECT l.rally " +
            "FROM LikedRally l " +
            "WHERE l.createdAt >= :startDate " +
            "GROUP BY l.rally " +
            "ORDER BY COUNT(l) DESC")
    List<Rally> findMonthlyTrendingRally(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT DISTINCT lr.rally.id "+
            "FROM LikedRally lr " +
            "WHERE lr.member.id = :memberId")
    List<Long> findDistinctRallyIdsByMember(@Param("memberId") Long memberId);
}
