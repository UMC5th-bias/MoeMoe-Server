package com.favoriteplace.app.controller;

import com.favoriteplace.app.dto.member.MemberDto.MemberLoginRequestDto;
import com.favoriteplace.app.dto.member.MemberDto.TokenInfo;
import com.favoriteplace.app.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class MemberController {
    private final MemberService memberService;


}
