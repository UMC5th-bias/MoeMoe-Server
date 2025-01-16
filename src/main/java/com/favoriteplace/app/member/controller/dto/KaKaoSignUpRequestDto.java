package com.favoriteplace.app.member.controller.dto;

import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.member.domain.enums.LoginType;
import com.favoriteplace.app.member.domain.enums.MemberStatus;
import com.favoriteplace.app.item.domain.Item;

public record KaKaoSignUpRequestDto(
        String nickname,
        Boolean snsAllow,
        String introduction
) {
    public Member toEntity(String profileImg, Item titleItem, String email) {
        return Member.builder()
                .nickname(nickname)
                .email(email)
                .alarmAllowance(snsAllow)
                .description(introduction)
                .profileImageUrl(profileImg)
                .point(0L)
                .loginType(LoginType.KAKAO)
                .profileTitle(titleItem)
                .status(MemberStatus.Y)
                .build();
    }
}
