package com.favoriteplace.app.community.controller.dto.comment;

import com.favoriteplace.app.member.controller.dto.UserInfoResponseDto;
import java.util.List;

public record CommentParentResponseDto(UserInfoResponseDto userInfo, Long id, String content, String passedTime,
                                       Boolean isWrite, List<CommentSubResponseDto> subComments) {
}
