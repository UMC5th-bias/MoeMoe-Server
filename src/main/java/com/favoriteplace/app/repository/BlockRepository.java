package com.favoriteplace.app.repository;

import com.favoriteplace.app.domain.Block;
import com.favoriteplace.app.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
    List<Block> findByMember(Member member);

    Boolean existsByMemberIdAndBlockedMemberId(Long memberId, Long blockedMemberId);

    void deleteByMemberIdAndBlockedMemberId(Long memberId, Long blockedMemberId);
}
