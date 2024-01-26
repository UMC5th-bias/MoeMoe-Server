package com.favoriteplace.app.repository;

import com.favoriteplace.app.domain.item.AcquiredItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcquiredItemRepository extends JpaRepository<AcquiredItem, Long> {
}
