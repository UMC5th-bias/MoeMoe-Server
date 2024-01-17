package com.favoriteplace.app.repository;

import com.favoriteplace.app.domain.travel.Rally;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RallyRepository extends JpaRepository<Rally, Long> {
}
