package com.favoriteplace.app.repository;

import com.favoriteplace.app.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsById(Long id);

}
