package com.favoriteplace.app.community.controller.dto.comment;

public record CommentCreateRequestDto(Long parentCommentId, Long referenceCommentId, String content) {
}
