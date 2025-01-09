package com.favoriteplace.app.item.repository;

import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.item.domain.enums.ItemType;
import com.favoriteplace.app.item.domain.AcquiredItem;
import com.favoriteplace.app.item.domain.Item;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcquiredItemRepository extends JpaRepository<AcquiredItem, Long> {
    List<AcquiredItem> findByMemberAndItem_Type(Member member, ItemType itemType);

    Optional<AcquiredItem> findByMemberAndItem(Member member, Item item);
}
