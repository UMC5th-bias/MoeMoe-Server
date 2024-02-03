package com.favoriteplace.app.dto;

import com.favoriteplace.app.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponseDto {
    private Long id;
    private String nickname;
    private String profileImageUrl;
    private String profileTitleUrl;
    private String profileIconUrl;

    public static UserInfoResponseDto of(Member member) {
        return UserInfoResponseDto.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImageUrl())
                .profileTitleUrl(member.getProfileTitle()!= null ? member.getProfileTitle().getImage().getUrl() : null)
                .profileIconUrl(member.getProfileIcon()!= null ? member.getProfileIcon().getImage().getUrl() : null)
                .build();
    }
}
