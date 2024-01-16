package com.favoriteplace.app.service;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.dto.HomeResponseDto;
import com.favoriteplace.app.repository.MemberRepository;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    //private final JwtUtil jwtUtil;

    @Transactional
    public boolean isTokenExists(String accessToken) {
        /*
        [JWT]
        String userId = jwtUtil.getUserIdFromToken(accessToken);
        return memberRepository.existsById(Long.valueOf(userId));
        */
        //[임시] : accessToken 대신 memberId 사용
        return memberRepository.existsById(Long.valueOf(accessToken));
    }

    @Transactional
    public HomeResponseDto.UserInfo getUserInfo(String accessToken) {
        /*
        [JWT]
        String userId = jwtUtil.getUserIdFromToken(accessToken);
        Member member = memberRepository.findById(Long.valueOf(accessToken))
                .orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_FOUND));
        return HomeResponseDto.UserInfo.of(member);
         */
        //[임시] : accessToken 대신 memberId 사용
        Member member = memberRepository.findById(Long.valueOf(accessToken))
                .orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_FOUND));
        return HomeResponseDto.UserInfo.of(member);
    }
}
