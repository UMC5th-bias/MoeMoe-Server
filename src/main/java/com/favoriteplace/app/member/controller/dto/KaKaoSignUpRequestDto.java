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
}
