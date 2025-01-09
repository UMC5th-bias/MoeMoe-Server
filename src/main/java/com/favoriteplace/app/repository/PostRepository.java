package com.favoriteplace.app.repository;

import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.domain.community.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Long countByMember(Member member);
}
