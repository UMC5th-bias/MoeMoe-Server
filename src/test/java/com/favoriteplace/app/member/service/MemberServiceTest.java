//package com.favoriteplace.app.member.service;
//
//import com.favoriteplace.app.member.controller.dto.AuthKakaoLoginDto;
//import com.favoriteplace.app.member.controller.dto.TokenInfoDto;
//import com.favoriteplace.app.member.domain.Member;
//import com.favoriteplace.app.member.repository.MemberRepository;
//import com.favoriteplace.app.member.service.MemberService;
//import com.favoriteplace.global.auth.kakao.KakaoClient;
//import com.favoriteplace.global.auth.provider.JwtTokenProvider;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
//
//
//@ExtendWith(MockitoExtension.class)
//@ActiveProfiles("test")
//class MemberServiceTest {
//
//    @Mock
//    private KakaoClient kakaoClient;
//
//    @Mock
//    private MemberRepository memberRepository;
//
//    @Mock
//    private JwtTokenProvider jwtTokenProvider;
//
//    @InjectMocks
//    private MemberService memberService;
//
//    private static Member member;
//
//    @BeforeEach
//    void setUser() {
//        member = Member.create(
//                "yoon",
//                "email",
//                true,
//                "자기소개",
//                "image",
//                null);
//    }
//
//    @Test
//    @DisplayName("카카오 로그인 외부 통신에 성공하면 토큰을 생성 후 반환한다.")
//    void kakakoLogin() {
//        // given
//        when(kakaoClient.getUserInfo(anyString()))
//                .thenReturn(new AuthKakaoLoginDto(new AuthKakaoLoginDto.KakaoAccount("email")));
//
//        when(memberRepository.findByEmail("email"))
//                .thenReturn(Optional.of(member));
//
//        when(jwtTokenProvider.issueAccessToken(anyString()))
//                .thenReturn(TokenInfoDto.of("accessToken", "refreshToken"));
//
//        // when
//        TokenInfoDto token = memberService.kakaoLogin("kakaoAccessToken");
//
//        // then
//        assertThat(token.accessToken()).isNotBlank();
//        assertThat(token.refreshToken()).isNotBlank();
//    }
//
//}