package com.favoriteplace.app.repository;

import com.favoriteplace.app.domain.travel.Address;
import com.favoriteplace.app.domain.travel.Rally;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByPilgrimages_Rally(Rally rally);
    Address findByStateAndDistrict(String state, String district);
    @Query("SELECT distinct a FROM Address a WHERE a.state LIKE %:keyword% OR a.district LIKE %:keyword%")
    List<Address> findByStateOrDistrictContaining(@Param("keyword") String keyword);
}
