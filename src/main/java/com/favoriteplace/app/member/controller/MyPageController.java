package com.favoriteplace.app.member.controller;

import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.common.dto.CommonResponseDto;
import com.favoriteplace.app.member.controller.dto.MyPageDto;
import com.favoriteplace.app.member.controller.dto.MyPageDto.MyFcmTokenDto;
import com.favoriteplace.app.member.service.MyPageCommandService;
import com.favoriteplace.app.member.service.MyPageQueryService;
import com.favoriteplace.global.util.SecurityUtil;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/my")
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageQueryService myPageQueryService;
    private final MyPageCommandService myPageCommandService;
    private final SecurityUtil securityUtil;

    // 내 정보 (메인)
    @GetMapping("")
    public MyPageDto.MyInfoDto getMyInfo() {
        Member member = securityUtil.getUser();
        return myPageQueryService.getMyInfo(member);
    }

    // 프로필 조회(사용자 정보)
    @GetMapping("/profile")
    public MyPageDto.MyProfileDto getMyProfile() {
        Member member = securityUtil.getUser();
        return myPageQueryService.getMyProfile(member);
    }

    // 보유 아이템 조회
    @GetMapping("/items")
    public MyPageDto.MyItemDto getMyItems(@RequestParam(value = "type", defaultValue = "title") String type) {
        Member member = securityUtil.getUser();
        return myPageQueryService.getMyItems(member, type);
    }

    // 아이템 착용
    @PutMapping("/items/{item_id}")
    public CommonResponseDto.PostResponseDto wearItem(@PathVariable("item_id") Long itemId) {
        Member member = securityUtil.getUser();
        return myPageCommandService.wearItem(itemId, member);
    }

    // 차단한 사용자 목록
    @GetMapping("/blocks")
    public List<MyPageDto.MyBlockDto> getMyBlock() {
        Member member = securityUtil.getUser();
        return myPageQueryService.getMyBlock(member);
    }

    // 찜한 성지순례
    @GetMapping("/guestbooks/like")
    public List<MyPageDto.MyGuestBookDto> getMyLikedBook() {
        Member member = securityUtil.getUser();
        return myPageQueryService.getMyLikedBook(member);
    }

    // 인증한 성지순례
    @GetMapping("/guestbooks/visited")
    public List<MyPageDto.MyGuestBookDto> getMyVisitedBook() {
        Member member = securityUtil.getUser();
        return myPageQueryService.getMyVisitedBook(member);
    }

    // 완료한 성지순례
    @GetMapping("/guestbooks/done")
    public List<MyPageDto.MyGuestBookDto> getMyDoneBook() {
        Member member = securityUtil.getUser();
        return myPageQueryService.getMyDoneBook(member);
    }

    //사용자 block하기
    @PostMapping("/blocks/{member_id}")
    public MyPageDto.MyModifyBlockDto modifyMemberBlock(
            @PathVariable("member_id") Long blockedMember
    ) {
        Member member = securityUtil.getUser();
        return myPageCommandService.modifyMemberBlock(member, blockedMember);
    }

    //FCM token 등록 & 변경
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204")
    })
    @PatchMapping("/fcmToken")
    public ResponseEntity<?> modifyFcmToken(
            @Valid @RequestBody MyFcmTokenDto request
    ) {
        Member member = securityUtil.getUser();
        myPageCommandService.modifyFcmToken(member, request);
        return ResponseEntity.noContent().build();
    }
}