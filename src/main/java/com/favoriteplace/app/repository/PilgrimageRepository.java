package com.favoriteplace.app.repository;

import com.favoriteplace.app.domain.travel.Pilgrimage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PilgrimageRepository extends JpaRepository<Pilgrimage, Long> {
}
