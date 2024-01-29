package com.favoriteplace.app.repository;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.travel.CompleteRally;
import com.favoriteplace.app.domain.travel.Rally;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompleteRallyRepository extends JpaRepository<CompleteRally, Long> {
    CompleteRally findByMemberAndRally(Member member, Rally rally);
}
