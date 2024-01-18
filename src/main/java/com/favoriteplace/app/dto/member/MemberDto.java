package com.favoriteplace.app.dto.member;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.enums.LoginType;
import com.favoriteplace.app.domain.enums.MemberStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberDto {


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberSignUpReqDto {
        public String nickname;
        public String email;
        public String password;
        public Boolean snsAllow;
        public String introduction;

        public Member toEntity(String encodedPassword, String profileImg) {
            return Member.builder()
                .nickname(nickname)
                .email(email)
                .password(encodedPassword)
                .alarmAllowance(snsAllow)
                .description(introduction)
                .profileImageUrl(profileImg)
                .point(0L)
                .loginType(LoginType.SELF)
                .status(MemberStatus.Y)
                .build();
        }
    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class TokenInfo {

        private String grantType;
        private String accessToken;
        private String refreshToken;
    }

}
