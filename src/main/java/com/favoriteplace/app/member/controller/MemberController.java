package com.favoriteplace.app.member.controller;

import com.favoriteplace.app.member.Facade.AuthFacade;
import com.favoriteplace.app.member.controller.dto.TokenInfoDto;
import com.favoriteplace.global.auth.provider.JwtTokenProvider;
import com.favoriteplace.app.member.controller.dto.KaKaoSignUpRequestDto;
import com.favoriteplace.app.member.controller.dto.MemberDto;
import com.favoriteplace.app.member.controller.dto.MemberDto.EmailCheckReqDto;
import com.favoriteplace.app.member.controller.dto.MemberDto.EmailDuplicateResDto;
import com.favoriteplace.app.member.controller.dto.MemberDto.EmailSendResDto;
import com.favoriteplace.app.member.controller.dto.MemberSignUpReqDto;
import com.favoriteplace.app.member.service.MailSendService;
import com.favoriteplace.app.member.service.MemberService;
import com.favoriteplace.global.util.SecurityUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.io.IOException;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class MemberController {
    private final AuthFacade authFacade;
    private final MailSendService mailSendService;
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityUtil securityUtil;

    @PostMapping("/login/kakao")
    public ResponseEntity<TokenInfoDto> kakaoLogin(
            @RequestHeader("Authorization") final String token
    ) {
        return ResponseEntity.ok(authFacade.kakaoLogin(token));
    }

    @PostMapping("/signup/kakao")
    public ResponseEntity<MemberDto.MemberSignUpResDto> kakaoSignUp(
            @RequestHeader("Authorization") final String token,
            @RequestPart(required = false) final List<MultipartFile> images,
            @RequestPart final KaKaoSignUpRequestDto data
    ) throws IOException {
        return ResponseEntity.ok(authFacade.kakaoSignUp(token, data, images));
    }

    @PostMapping("/signup")
    public ResponseEntity<MemberDto.MemberSignUpResDto> signup(
            @RequestPart(required = false) List<MultipartFile> images,
            @RequestPart MemberSignUpReqDto data
    ) throws IOException {
        return ResponseEntity.ok(authFacade.signup(data, images));
    }

    @PostMapping("/signup/email")
    public ResponseEntity<EmailSendResDto> emailCheck(
            @RequestBody @Valid MemberDto.EmailSendReqDto reqDto
    ) {
        return ResponseEntity.ok(mailSendService.joinEmail(reqDto.getEmail()));
    }

    @PostMapping("/signup/email/check")
    public ResponseEntity<Void> authCheck(
            @RequestBody @Valid EmailCheckReqDto reqDto
    ) {
        mailSendService.checkAuthNum(reqDto.getEmail(), reqDto.getAuthNum().toString());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signup/email/duplicate")
    public ResponseEntity<EmailDuplicateResDto> emailDuplicateCheck(
            @RequestBody @Valid MemberDto.EmailSendReqDto reqDto
    ) {
        return ResponseEntity.ok(memberService.emailDuplicateCheck(reqDto));
    }

    @PostMapping("/password/new")
    public ResponseEntity<String> setNewPassword(
            @RequestParam("email") String email,
            @RequestParam("password") String password
    ) {
        memberService.setNewPassword(email, password);
        return ResponseEntity.ok("성공했습니다.");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String accessToken = securityUtil.resolveToken(request);
        authFacade.logout(accessToken);
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }
}
