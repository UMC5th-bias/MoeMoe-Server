package com.favoriteplace.app.item.repository;

import com.favoriteplace.app.domain.enums.ItemType;
import com.favoriteplace.app.domain.enums.SaleStatus;
import com.favoriteplace.app.item.domain.Item;
import jakarta.persistence.QueryHint;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByStatus(SaleStatus status);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    @Query("SELECT i from Item i WHERE (i.category = 'NEW' or i.createdAt >= :now) and i.type = :type")
    List<Item> findAllByNEWCategory(@Param("type") ItemType type, @Param("now") LocalDateTime now);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    @Query("SELECT it from Item it join fetch it.defaultImage im where it.id = :item_id")
    Optional<Item> findAllByIdWithImage(@Param("item_id") Long itemID);

    Optional<Item> findByName(String name);

    @Modifying
    @Query("update Item i set i.category = 'NORMAL' where i.category = 'NEW' and i.createdAt < :now")
    void changeCategory(@Param("now") LocalDateTime now);

}
