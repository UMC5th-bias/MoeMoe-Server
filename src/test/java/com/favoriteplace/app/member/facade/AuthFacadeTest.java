package com.favoriteplace.app.member.facade;

import com.favoriteplace.app.member.Facade.AuthFacade;
import com.favoriteplace.app.member.controller.dto.AuthKakaoLoginDto;
import com.favoriteplace.app.member.controller.dto.TokenInfoDto;
import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.member.domain.enums.LoginType;
import com.favoriteplace.app.member.service.MemberService;
import com.favoriteplace.global.auth.kakao.KakaoClient;
import com.favoriteplace.global.auth.provider.JwtTokenProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthFacadeTest {

    @Mock
    private MemberService memberService;

    @Mock
    private KakaoClient kakaoClient;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthFacade authFacade;

    private static Member member;

    @BeforeEach
    void setUp() {
        member = Member.create(
                "yoon",
                "email",
                true,
                "자기소개",
                "image",
                null,
                LoginType.SELF
        );
    }

    @Test
    @DisplayName("카카오 로그인 - 카카오로부터 사용자 정보를 받아오고, 토큰을 생성하여 반환한다.")
    void kakaoLoginTest() {
        // given
        when(kakaoClient.getUserInfo(anyString()))
                .thenReturn(new AuthKakaoLoginDto(new AuthKakaoLoginDto.KakaoAccount("email")));

        doNothing().when(memberService).kakaoLogin("email");

        when(jwtTokenProvider.issueAccessToken("email")).thenReturn("accessToken");
        when(jwtTokenProvider.issueRefreshToken("email")).thenReturn("refreshToken");

        // when
        TokenInfoDto result = authFacade.kakaoLogin("kakaoAccessToken");

        // then
        assertThat(result.accessToken()).isEqualTo("accessToken");
        assertThat(result.refreshToken()).isEqualTo("refreshToken");

        verify(kakaoClient).getUserInfo("kakaoAccessToken");
        verify(memberService).kakaoLogin("email");
        verify(jwtTokenProvider).issueAccessToken("email");
        verify(jwtTokenProvider).issueRefreshToken("email");
    }
}
