package com.favoriteplace.app.notification.repository;

import com.favoriteplace.app.notification.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.member.id = :memberId")
    void readAllNotification(@Param("memberId") Long memberId);

    Page<Notification> findByMemberId(Long memberId, Pageable pageable);
}
