package com.favoriteplace.app.member.controller.dto;

import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.member.domain.enums.LoginType;
import com.favoriteplace.app.member.domain.enums.MemberStatus;
import com.favoriteplace.app.item.domain.Item;

import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberDto {
    @Builder
    @Getter
    @AllArgsConstructor
    public static class MemberSignUpResDto {
        private String nickname;
        private String introduction;
        private String profileImage;
        private String profileTitleItem;
        private String accessToken;
        private String refreshToken;

        public static MemberSignUpResDto from(Member member, TokenInfoDto tokenInfo) {
            return MemberSignUpResDto.builder()
                    .nickname(member.getNickname())
                    .introduction(member.getDescription())
                    .profileImage(member.getProfileImageUrl())
                    .profileTitleItem(member.getProfileTitle().getDefaultImage().getUrl())
                    .accessToken(tokenInfo.accessToken())
                    .refreshToken(tokenInfo.refreshToken())
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
        @NotEmpty(message = "이메일 입력은 필수 입니다.")
        @Pattern(
                regexp = "^[\\w\\.-]+@[\\w\\.-]+\\.[a-zA-Z]{2,}$",
                message = "이메일 형식에 맞지 않습니다."
        )
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

        @NotEmpty(message = "이메일을 입력해 주세요")
        @Pattern(
                regexp = "^[\\w\\.-]+@[\\w\\.-]+\\.[a-zA-Z]{2,}$",
                message = "이메일 형식에 맞지 않습니다."
        )
        private String email;

        @NotNull(message = "인증 번호를 입력해 주세요")
        private Integer authNum;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class MemberInfo {
        private Integer id;
        private String nickname;
        private int point;
        private String profileImageUrl;
        private String profileTitleUrl;
        private String profileIconUrl;

        public static MemberInfo from(Member member) {
            System.out.println(member.getProfileIcon());
            return MemberInfo.builder()
                .id(member == null ? null : member.getId().intValue())
                .nickname(member == null ? null : member.getNickname())
                .point(member == null ? null : Long.valueOf(member.getPoint()).intValue())
                .profileImageUrl(member.getProfileImageUrl() == null ? null : member.getProfileImageUrl())
                .profileIconUrl(member.getProfileIcon() == null ? null : member.getProfileIcon().getDefaultImage().getUrl())
                .profileTitleUrl(member.getProfileTitle() == null ? null : member.getProfileTitle().getDefaultImage().getUrl())
                .build();
        }
    }

}
