package com.favoriteplace.app.community.controller.dto.comment;

import com.favoriteplace.app.member.controller.dto.UserInfoResponseDto;

public record CommentSubResponseDto(UserInfoResponseDto userInfo, Long id, String content, String passedTime,
                                    Boolean isWrite, String referenceNickname) {
}
