package com.favoriteplace.app.service;

import com.favoriteplace.app.converter.MyPageConverter;
import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.item.AcquiredItem;
import com.favoriteplace.app.dto.MyPageDto;
import com.favoriteplace.app.repository.AcquiredItemRepository;
import com.favoriteplace.app.repository.ItemRepository;
import com.favoriteplace.app.repository.MemberRepository;
import jakarta.websocket.server.ServerEndpoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageQueryService {
    private final MemberRepository memberRepository;

    public MyPageDto.MyInfoDto getMyInfo(Member member) {

        return null;
    }

    public MyPageDto.MyProfileDto getMyProfile(Member member) {
        return MyPageConverter.toMyProfileDto(member);
    }

    public MyPageDto.MyItemDto getMyItems(Member member, String type) {
        return null;
    }

    public MyPageDto.MyGuestBookDto getMyLikedBook(Member member) {
        return null;
    }

    public MyPageDto.MyGuestBookDto getMyVisitedBook(Member member) {
        return null;
    }

    public MyPageDto.MyGuestBookDto getMyDoneBook(Member member) {
        return null;
    }

    public MyPageDto.MyBlockDto getMyBlock(Member member) {
        return null;
    }
}
