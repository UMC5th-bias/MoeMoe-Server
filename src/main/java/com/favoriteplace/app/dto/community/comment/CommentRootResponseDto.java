package com.favoriteplace.app.dto.community.comment;

import java.util.List;

public record CommentRootResponseDto(Long page, Long size, List<CommentParentResponseDto> parentComment) {
}
