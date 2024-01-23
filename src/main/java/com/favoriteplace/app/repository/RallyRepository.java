package com.favoriteplace.app.repository;

import com.favoriteplace.app.domain.travel.Rally;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RallyRepository extends JpaRepository<Rally, Long> {
    @Query("SELECT r FROM Rally r ORDER BY r.createdAt")
    List<Rally> findAllOrderByCreatedAt();
}
