package com.favoriteplace.app.controller;

import com.favoriteplace.app.dto.member.MemberDto;
import com.favoriteplace.app.dto.member.MemberDto.MemberSignUpReqDto;
import com.favoriteplace.app.service.MemberService;
import com.favoriteplace.global.security.provider.JwtTokenProvider;
import com.favoriteplace.global.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityUtil securityUtil;

    @PostMapping("/signup")
    public ResponseEntity<MemberDto.TokenInfo> signup(@RequestPart(required = false) List<MultipartFile> images, @RequestPart
        MemberSignUpReqDto data, HttpServletRequest request) {
        return ResponseEntity.ok(memberService.signup(data));
    }

    //사용자가 로그인하지 않아도 접근할 수 있는 endpoint
    @PostMapping("/hi")
    public void hi(HttpServletRequest request) {
        System.out.println(securityUtil.getUserFromHeader(request));
    }

    //사용자가 로그인해야 접근할 수 있는 endpoint
    @GetMapping("/hi")
    public void nice(HttpServletRequest request) {
        securityUtil.getUser();
    }

}
