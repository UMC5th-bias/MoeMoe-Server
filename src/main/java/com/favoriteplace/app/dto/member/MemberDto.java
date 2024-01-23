package com.favoriteplace.app.dto.member;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.enums.LoginType;
import com.favoriteplace.app.domain.enums.MemberStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @Getter
    @NoArgsConstructor
    public static class EmailSendReqDto {
        /**
         * 1)@기호를 포함해야 한다.
         * 2)@기호를 기준으로 이메일 주소를 이루는 로컬호스트와 도메인 파트가 존재해야 한다.
         * 3)도메인 파트는 최소하나의 점과 그 뒤에 최소한 2개의 알파벳을 가진다를 검증
         */
        @Email
        @NotEmpty(message = "이메일을 입력해주세요")
        private String email;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class EmailSendResDto {
        private Integer authNum;
    }

    @Getter
    @AllArgsConstructor
    public static class EmailDuplicateResDto {
        private Boolean isExists;
    }


    @Getter
    @NoArgsConstructor
    public static class EmailCheckReqDto {
        @Email
        @NotEmpty(message = "이메일을 입력해 주세요")
        private String email;

        @NotNull(message = "인증 번호를 입력해 주세요")
        private Integer authNum;
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
