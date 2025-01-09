package com.favoriteplace.app.dto.community.comment;

import com.favoriteplace.app.member.controller.dto.UserInfoResponseDto;

public record CommentSubResponseDto(UserInfoResponseDto userInfo, Long id, String content, String passedTime,
                                    Boolean isWrite, String referenceNickname) {
}
