package com.favoriteplace.app.controller;

import com.favoriteplace.app.dto.MyPageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/my")
@RequiredArgsConstructor
public class MyPageController {


    // 내 정보 (메인)
    @GetMapping("")
    public MyPageDto.MyInfoDto getMyInfo(){
        return null;
    }

    // 프로필 조회(사용자 정보)
    @GetMapping("/profile")
    public MyPageDto.MyProfileDto getMyProfile(){
        return null;
    }

    // 보유 아이템 조
    @GetMapping("/items")
    public MyPageDto.MyItemDto getMyItems(){
        return null;
    }

    // 찜한 성지순례
    @GetMapping("/guestbooks/like")
    public MyPageDto.MyGuestBookDto getMyLikedBook(){
        return null;
    }

    // 인증한 성지순례
    @GetMapping("/guestbooks/visited")
    public MyPageDto.MyGuestBookDto getMyVisitedBook(){
        return null;
    }

    // 완료한 성지순례
    @GetMapping("/guestbooks/done")
    public MyPageDto.MyGuestBookDto getMyDoneBook(){
        return null;
    }

    // 차단한 사용자 목록
    @GetMapping("/blocks")
    public MyPageDto.MyBlockDto getMyBlock(){
        return null;
    }
}
