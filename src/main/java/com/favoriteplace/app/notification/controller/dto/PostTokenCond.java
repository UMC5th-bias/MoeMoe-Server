package com.favoriteplace.app.notification.controller.dto;

import com.favoriteplace.app.notification.service.TokenMessage;
import lombok.Builder;

@Builder
public record PostTokenCond(
        String token,
        TokenMessage tokenMessage,
        Long postId,
        Long guestBookId,
        String message,
        Long notificationId
) {
}
