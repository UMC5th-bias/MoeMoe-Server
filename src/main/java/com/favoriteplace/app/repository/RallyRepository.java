package com.favoriteplace.app.repository;

import com.favoriteplace.app.pilgrimage.domain.Pilgrimage;
import com.favoriteplace.app.domain.travel.Rally;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RallyRepository extends JpaRepository<Rally, Long> {
    @Query("SELECT r FROM Rally r ORDER BY r.createdAt")
    List<Rally> findAllOrderByCreatedAt();

    @Query("select r from Rally r where :pilgrimage MEMBER of r.pilgrimages")
    Rally findByPilgrimage(Pilgrimage pilgrimage);

    @Query("select r from Rally r where r.name like %:name%")
    List<Rally> findByName(String name);
}
