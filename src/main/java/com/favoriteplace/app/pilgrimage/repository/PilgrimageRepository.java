package com.favoriteplace.app.pilgrimage.repository;

import com.favoriteplace.app.rally.domain.Address;
import com.favoriteplace.app.pilgrimage.domain.Pilgrimage;
import com.favoriteplace.app.rally.domain.Rally;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PilgrimageRepository extends JpaRepository<Pilgrimage, Long> {
    List<Pilgrimage> findByRallyId(Long rallyId);
    List<Pilgrimage> findByRallyAndAddress(Rally rally, Address address);
    List<Pilgrimage> findByAddress(Address address);
}
