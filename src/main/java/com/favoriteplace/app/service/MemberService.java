package com.favoriteplace.app.service;

import com.favoriteplace.app.dto.member.MemberDto.TokenInfo;
import com.favoriteplace.app.repository.MemberRepository;
import com.favoriteplace.global.security.provider.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {


}
