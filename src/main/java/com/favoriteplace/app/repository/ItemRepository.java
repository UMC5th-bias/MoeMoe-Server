package com.favoriteplace.app.repository;

import com.favoriteplace.app.domain.enums.ItemType;
import com.favoriteplace.app.domain.enums.SaleStatus;
import com.favoriteplace.app.domain.item.Item;
import jakarta.persistence.QueryHint;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByStatus(SaleStatus status);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    @Query("SELECT i from Item i WHERE i.category = 'NEW' and i.type = :type")
    List<Item> findAllByNEWCategory(@Param("type") ItemType type);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    @Query("SELECT it from Item it join fetch it.image im where it.id = :item_id")
    Optional<Item> findAllByIdWithImage(@Param("item_id") Long itemID);

}
