package com.favoriteplace.app.service;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.dto.member.MemberDto;
import com.favoriteplace.app.dto.member.MemberDto.MemberSignUpReqDto;
import com.favoriteplace.app.dto.member.MemberDto.TokenInfo;
import com.favoriteplace.app.repository.MemberRepository;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.security.provider.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public MemberDto.TokenInfo signup(MemberSignUpReqDto memberSignUpReqDto) {
        String password = passwordEncoder.encode(memberSignUpReqDto.getPassword());
        Member member = memberSignUpReqDto.toEntity(password, null);

        //이메일 중복 여부 확인
        if (findMember(memberSignUpReqDto.getEmail())!=null) {
            throw new RestApiException(ErrorCode.USER_ALREADY_EXISTS);
        }

        memberRepository.save(member);
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(memberSignUpReqDto.getEmail());

        member.updateRefreshToken(tokenInfo.getRefreshToken());
        return tokenInfo;
    }

    @Transactional
    public Member findMember(String email) {
        return memberRepository.findByEmail(email)
            .orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_FOUND));

    }

}
