package com.favoriteplace.app.member.Facade;

import com.favoriteplace.app.member.controller.dto.KaKaoSignUpRequestDto;
import com.favoriteplace.app.member.controller.dto.MemberDto;
import com.favoriteplace.app.member.controller.dto.MemberSignUpReqDto;
import com.favoriteplace.app.member.controller.dto.TokenInfoDto;
import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.member.service.MemberService;
import com.favoriteplace.app.member.service.TokenService;
import com.favoriteplace.global.auth.kakao.KakaoClient;
import com.favoriteplace.global.auth.provider.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthFacade {
    private final MemberService memberService;
    private final TokenService tokenService;
    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoClient kakaoClient;

    public TokenInfoDto kakaoLogin(final String token) {
        String email = kakaoClient.getUserInfo(token).kakaoAccount().email();
        memberService.kakaoLogin(email);
        return getTokenDto(email);
    }

    public MemberDto.MemberSignUpResDto kakaoSignUp(
            final String token,
            final KaKaoSignUpRequestDto memberSignUpReqDto,
            final List<MultipartFile> images
    ) throws IOException {
        String email = kakaoClient.getUserInfo(token).kakaoAccount().email();
        TokenInfoDto tokenDto = getTokenDto(email);
        Member member = memberService.kakaoSignUp(email, memberSignUpReqDto, images);
        tokenService.updateRefreshToken(member, tokenDto.refreshToken());
        return MemberDto.MemberSignUpResDto.from(member, tokenDto);
    }

    public MemberDto.MemberSignUpResDto signup(
            final MemberSignUpReqDto memberSignUpReqDto,
            final List<MultipartFile> images
    ) throws IOException {
        Member member = memberService.signup(memberSignUpReqDto, images);
        TokenInfoDto tokenDto = getTokenDto(member.getEmail());
        tokenService.updateRefreshToken(member, tokenDto.refreshToken());
        return MemberDto.MemberSignUpResDto.from(member, tokenDto);
    }

    public void logout(String accessToken) {
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        String email = authentication.getName();
        Long expiration = jwtTokenProvider.getExpiration(accessToken);
        Member member = memberService.findMember(email);
        tokenService.logoutAndInvalidateToken(member, expiration, accessToken);
    }

    private TokenInfoDto getTokenDto(String email) {
        return TokenInfoDto.of(
                jwtTokenProvider.issueAccessToken(email),
                jwtTokenProvider.issueRefreshToken(email)
        );
    }
}
