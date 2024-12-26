package com.favoriteplace.app.dto.community.comment;

import lombok.Builder;

public record CommentCreateRequestDto(Long parentCommentId, Long referenceCommentId, String content) {
}
