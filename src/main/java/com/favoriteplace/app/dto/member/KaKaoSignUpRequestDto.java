package com.favoriteplace.app.dto.member;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.enums.LoginType;
import com.favoriteplace.app.domain.enums.MemberStatus;
import com.favoriteplace.app.domain.item.Item;
import com.favoriteplace.global.gcpImage.ConvertUuidToUrl;

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
