package com.favoriteplace.app.service;

import static com.favoriteplace.global.exception.ErrorCode.USER_ALREADY_EXISTS;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.dto.HomeResponseDto;
import com.favoriteplace.app.dto.member.MemberDto;
import com.favoriteplace.app.dto.member.MemberDto.EmailCheckReqDto;
import com.favoriteplace.app.dto.member.MemberDto.EmailSendReqDto;
import com.favoriteplace.app.dto.member.MemberDto.MemberSignUpReqDto;
import com.favoriteplace.app.dto.member.MemberDto.TokenInfo;
import com.favoriteplace.app.repository.MemberRepository;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.security.provider.JwtTokenProvider;
import com.favoriteplace.global.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtil securityUtil;

    @Transactional
    public MemberDto.TokenInfo signup(MemberSignUpReqDto memberSignUpReqDto) {
        String password = passwordEncoder.encode(memberSignUpReqDto.getPassword());
        Member member = memberSignUpReqDto.toEntity(password, null);

        //TODO 유저 프로필 이미지 + 새싹회원 칭호 저장
        memberRepository.save(member);
        return null;
    }

    @Transactional
    public void emailDuplicateCheck(EmailSendReqDto emailSendReqDto) {
        String email = emailSendReqDto.getEmail();
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new RestApiException(USER_ALREADY_EXISTS));

    }

    @Transactional
    public boolean isTokenExists(HttpServletRequest request) {
        return securityUtil.getUserFromHeader(request) != null;
    }

    @Transactional
    public HomeResponseDto.UserInfo getUserInfo(HttpServletRequest request) {
        return HomeResponseDto.UserInfo.of(securityUtil.getUserFromHeader(request));
    }
}