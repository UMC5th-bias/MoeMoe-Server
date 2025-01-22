package com.favoriteplace.app.community.controller.dto.comment;

import java.util.List;

public record CommentRootResponseDto(Long page, Long size, List<CommentParentResponseDto> parentComment) {
}
