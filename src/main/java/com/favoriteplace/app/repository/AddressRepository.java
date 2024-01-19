package com.favoriteplace.app.repository;

import com.favoriteplace.app.domain.travel.Address;
import com.favoriteplace.app.domain.travel.Rally;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByPilgrimages_Rally(Rally rally);
}
