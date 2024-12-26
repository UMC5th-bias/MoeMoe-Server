package com.favoriteplace.app.dto.community;

import lombok.Builder;

@Builder
public record CommentCreateRequestDto(Long parentCommentId, Long referenceCommentId, String content) {
}
