package com.favoriteplace.app.repository;

import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.domain.community.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Long countByMember(Member member);
    Boolean existsByParentComment(Comment parentComment);
    Boolean existsByReferenceComment(Comment referenceComment);
}
