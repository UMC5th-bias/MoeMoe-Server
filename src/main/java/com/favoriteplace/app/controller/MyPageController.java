package com.favoriteplace.app.controller;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.dto.MyPageDto;
import com.favoriteplace.app.service.MyPageQueryService;
import com.favoriteplace.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/my")
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageQueryService myPageQueryService;
    private final SecurityUtil securityUtil;

    // !!!마이페이지 내 모든 API 회원전용!!!! -> 필터 추가 완료
    // 내 정보 (메인)
    @GetMapping("")
    public MyPageDto.MyInfoDto getMyInfo(){
        Member member = securityUtil.getUser();
        return myPageQueryService.getMyInfo(member);
    }

    // 프로필 조회(사용자 정보)
    @GetMapping("/profile")
    public MyPageDto.MyProfileDto getMyProfile(){
        Member member = securityUtil.getUser();
        return myPageQueryService.getMyProfile(member);
    }

    // 보유 아이템 조회
    // 쿼리스트링 필요
    @GetMapping("/items")
    public MyPageDto.MyItemDto getMyItems(@RequestParam("type")String type){
        Member member = securityUtil.getUser();
        return myPageQueryService.getMyItems(member, type);
    }

    // 찜한 성지순례
    @GetMapping("/guestbooks/like")
    public MyPageDto.MyGuestBookDto getMyLikedBook(){
        Member member = securityUtil.getUser();
        return myPageQueryService.getMyLikedBook(member);
    }

    // 인증한 성지순례
    @GetMapping("/guestbooks/visited")
    public MyPageDto.MyGuestBookDto getMyVisitedBook(){
        Member member = securityUtil.getUser();
        return myPageQueryService.getMyVisitedBook(member);
    }

    // 완료한 성지순례
    @GetMapping("/guestbooks/done")
    public MyPageDto.MyGuestBookDto getMyDoneBook(){
        Member member = securityUtil.getUser();
        return myPageQueryService.getMyDoneBook(member);
    }

    // 차단한 사용자 목록
    @GetMapping("/blocks")
    public MyPageDto.MyBlockDto getMyBlock(){
        Member member = securityUtil.getUser();
        return myPageQueryService.getMyBlock(member);
    }
}
