package com.favoriteplace.app.service.fcm.dto;

import com.favoriteplace.app.service.fcm.enums.TokenMessage;
import lombok.Builder;

@Builder
public record PostTokenCond(
        String token,
        TokenMessage tokenMessage,
        Long postId,
        String message
) {
}
