package com.favoriteplace.app.controller;

import com.favoriteplace.app.dto.member.MemberDto;
import com.favoriteplace.app.dto.member.MemberDto.EmailCheckReqDto;
import com.favoriteplace.app.dto.member.MemberDto.EmailDuplicateResDto;
import com.favoriteplace.app.dto.member.MemberDto.EmailSendResDto;
import com.favoriteplace.app.dto.member.MemberDto.MemberDetailResDto;
import com.favoriteplace.app.dto.member.MemberDto.MemberSignUpReqDto;
import com.favoriteplace.app.dto.member.MemberDto.TokenInfo;
import com.favoriteplace.app.service.MailSendService;
import com.favoriteplace.app.service.MemberService;
import com.favoriteplace.global.security.provider.JwtTokenProvider;
import com.favoriteplace.global.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class MemberController {
    private final MemberService memberService;
    private final MailSendService mailSendService;
    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityUtil securityUtil;

    @PostMapping("/signup")
    public ResponseEntity<MemberDetailResDto> signup(@RequestPart(required = false) List<MultipartFile> images, @RequestPart
        MemberSignUpReqDto data) throws IOException {
        return ResponseEntity.ok(memberService.signup(data, images));
    }

    @PostMapping("/signup/email")
    public ResponseEntity<EmailSendResDto> emailCheck(@Valid @RequestBody MemberDto.EmailSendReqDto reqDto) {
        return ResponseEntity.ok(mailSendService.joinEmail(reqDto.getEmail()));
    }

    @PostMapping("/signup/email/check")
    public ResponseEntity<Void> authCheck(@RequestBody @Valid EmailCheckReqDto reqDto){
        mailSendService.checkAuthNum(reqDto.getEmail(), reqDto.getAuthNum().toString());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signup/email/duplicate")
    public ResponseEntity<EmailDuplicateResDto> emailDuplicateCheck(@RequestBody @Valid MemberDto.EmailSendReqDto reqDto) {
        return ResponseEntity.ok(memberService.emailDuplicateCheck(reqDto));
    }

    @PostMapping("/password/new")
    public ResponseEntity<String> setNewPassword(@RequestParam("email") String email, @RequestParam("password") String password) {
        memberService.setNewPassword(email, password);
        return ResponseEntity.ok("성공했습니다.");
    }
}
