package com.favoriteplace.app.repository;

import com.favoriteplace.app.domain.travel.Address;
import com.favoriteplace.app.domain.travel.Pilgrimage;
import com.favoriteplace.app.domain.travel.Rally;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PilgrimageRepository extends JpaRepository<Pilgrimage, Long> {
    List<Pilgrimage> findByRallyId(Long rallyId);
    List<Pilgrimage> findByRallyAndAddress(Rally rally, Address address);
    List<Pilgrimage> findByAddress(Address address);
}
