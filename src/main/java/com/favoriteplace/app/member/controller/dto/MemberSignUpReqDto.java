package com.favoriteplace.app.member.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record MemberSignUpReqDto(
        @NotBlank(message = "닉네임은 필수값입니다.")
        String nickname,
        String email,
        String password,
        Boolean snsAllow,
        String introduction
) {
}
