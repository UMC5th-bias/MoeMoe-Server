package com.favoriteplace.app.rally.repository;

import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.rally.domain.CompleteRally;
import com.favoriteplace.app.rally.domain.Rally;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompleteRallyRepository extends JpaRepository<CompleteRally, Long> {
    List<CompleteRally> findByMemberAndRally(Member member, Rally rally);
    List<CompleteRally> findByMember(Member member);
}
