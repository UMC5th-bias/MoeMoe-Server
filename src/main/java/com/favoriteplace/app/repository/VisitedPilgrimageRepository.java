package com.favoriteplace.app.repository;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.travel.Pilgrimage;
import com.favoriteplace.app.domain.travel.Rally;
import com.favoriteplace.app.domain.travel.VisitedPilgrimage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Repository
public interface VisitedPilgrimageRepository extends JpaRepository<VisitedPilgrimage, Long> {
    List<VisitedPilgrimage> findByMemberIdOrderByModifiedAtDesc(Long memberId);
    @Query("""
    select count(distinct p.id)
    from Rally r
    join Pilgrimage p on r.id = p.rally.id
    join VisitedPilgrimage vp on p.id = vp.pilgrimage.id
    and vp.member.id = :memberId and r.id = :rallyId
    """)
    Long findByDistinctCount(@Param("memberId") Long memberId, @Param("rallyId") Long rallyId);
    @Query("""
    select count(distinct p.id)
    from Rally r
    join Pilgrimage p on r.id = p.rally.id
    join VisitedPilgrimage vp on p.id = vp.pilgrimage.id
    and vp.member.id = :memberId
    """)
    Long findByVisitedCount(@Param("memberId")Long memberId);
    List<VisitedPilgrimage> findByPilgrimageAndMemberOrderByCreatedAtDesc(Pilgrimage pilgrimage, Member member);
    Long countByMemberIdAndPilgrimageIdIn(Long memberId, List<Long> pilgrimageIds);

}
