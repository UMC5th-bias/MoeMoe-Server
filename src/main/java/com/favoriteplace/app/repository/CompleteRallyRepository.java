package com.favoriteplace.app.repository;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.travel.CompleteRally;
import com.favoriteplace.app.domain.travel.Rally;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompleteRallyRepository extends JpaRepository<CompleteRally, Long> {
    CompleteRally findByMemberAndRally(Member member, Rally rally);
    List<CompleteRally> findByMember(Member member);
}
