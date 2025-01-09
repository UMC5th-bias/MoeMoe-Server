package com.favoriteplace.app.repository;

import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.domain.travel.Pilgrimage;
import com.favoriteplace.app.domain.travel.Rally;
import com.favoriteplace.app.domain.travel.VisitedPilgrimage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitedPilgrimageRepository extends JpaRepository<VisitedPilgrimage, Long> {
    @Query("""
    select vp from VisitedPilgrimage vp
    join Pilgrimage p on p.id = vp.pilgrimage.id
    join Rally r on r.id = p.rally.id
    join Image i on r.image.id = i.id
    and vp.member.id = :memberId
    order by vp.modifiedAt desc
    """)
    List<VisitedPilgrimage> findByMemberIdOrderByModifiedAtDesc(@Param("memberId")Long memberId);

    @Query("""
    select count(distinct p.id)
    from Rally r
    join Pilgrimage p on r.id = p.rally.id
    join VisitedPilgrimage vp on p.id = vp.pilgrimage.id
    and vp.member.id = :memberId and r.id = :rallyId
    """)
    Long findByDistinctCount(@Param("memberId") Long memberId, @Param("rallyId") Long rallyId);

    @Query("""
    select distinct r
    from Rally r
    join Pilgrimage p on r.id = p.rally.id
    join VisitedPilgrimage vp on p.id = vp.pilgrimage.id
    and vp.member.id = :memberId
    """)
    List<Rally> findByDistinctPilgrimage(@Param("memberId") Long memberId);

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
