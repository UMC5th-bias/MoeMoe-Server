package com.favoriteplace.app.repository;

import com.favoriteplace.app.domain.travel.Pilgrimage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PilgrimageRepository extends JpaRepository<Pilgrimage, Long> {
    List<Pilgrimage> findByRallyId(Long rallyId);
}
