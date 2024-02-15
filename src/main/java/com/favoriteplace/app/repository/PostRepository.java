package com.favoriteplace.app.repository;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.community.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByCreatedAtBetweenOrderByLikeCountDesc(LocalDateTime start, LocalDateTime end);

    //Page<Post> findAllByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    //Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    //Page<Post> findAllByOrderByLikeCountDesc(Pageable pageable);
    Long countByMember(Member member);

    @Query("select p from Post p where p.title like %:keyword% order by p.createdAt desc")
    Page<Post> searchByTitleUsingKeyword(@Param("keyword")String keyword, Pageable pageable);

    @Query("select p from Post p where p.member.nickname like %:keyword% order by p.createdAt desc")
    Page<Post> searchByNicknameUsingKeyword(@Param("keyword")String keyword, Pageable pageable);

    @Query("select p from Post p where p.content like %:keyword% order by p.createdAt desc")
    Page<Post> searchPostByContentUsingKeyword(@Param("keyword")String keyword, Pageable pageable);
}
