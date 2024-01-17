package com.favoriteplace.app.repository;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.travel.Rally;
import com.favoriteplace.app.domain.travel.VisitedPilgrimage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitedPilgrimageRepository extends JpaRepository<VisitedPilgrimage, Long> {
    List<VisitedPilgrimage> findByMemberIdOrderByModifiedAtDesc(Long memberId);
    List<VisitedPilgrimage> findByMemberAndPilgrimage_Rally(Member member, Rally rally);
    Long countByMemberIdAndPilgrimageIdIn(Long memberId, List<Long> pilgrimageIds);
}
