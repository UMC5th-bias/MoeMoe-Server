package com.favoriteplace.app.notification.controller.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record NotificationResponseDto(
        Integer page,
        Integer size,
        List<NotificationInfo> notifications
) {
    @Builder
    public record NotificationInfo(
            Long id,
            String type,
            String date,
            String title,
            String content,
            Long postId,
            Long guestBookId,
            Long rallyId,
            Boolean isRead
    ){ }
}
