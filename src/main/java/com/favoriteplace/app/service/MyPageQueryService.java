package com.favoriteplace.app.service;

import com.favoriteplace.app.converter.MyPageConverter;
import com.favoriteplace.app.domain.Block;
import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.enums.ItemType;
import com.favoriteplace.app.domain.enums.SaleStatus;
import com.favoriteplace.app.domain.item.AcquiredItem;
import com.favoriteplace.app.domain.travel.CompleteRally;
import com.favoriteplace.app.dto.CommonResponseDto;
import com.favoriteplace.app.domain.travel.LikedRally;
import com.favoriteplace.app.domain.travel.VisitedPilgrimage;
import com.favoriteplace.app.dto.MyPageDto;
import com.favoriteplace.app.repository.*;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageQueryService {
    private final AcquiredItemRepository acquiredItemRepository;
    private final BlockRepository blockRespotiroy;
    private final LikedRallyRepository likedRallyRepository;
    private final VisitedPilgrimageRepository visitedPilgrimageRepository;
    private final CompleteRallyRepository completeRallyRepository;
    private final CommentRepository commentRepository;
    private final GuestBookRepository guestBookRepository;
    private final PostRepository postRepository;

    public MyPageDto.MyInfoDto getMyInfo(Member member) {
        Long completeRalliesCount = Long.valueOf(completeRallyRepository.findByMember(member).size());
        Long visitedPilgrimagesCount = visitedPilgrimageRepository.findByVisitedCount(member.getId());
        Long postedCount = postRepository.countByMember(member)
                + guestBookRepository.countByMember(member);
        Long commentsCount = commentRepository.countByMember(member);
        return MyPageConverter
                .toMyInfoDto(completeRalliesCount, visitedPilgrimagesCount, postedCount, commentsCount);
    }

    public MyPageDto.MyProfileDto getMyProfile(Member member) {
        return MyPageConverter.toMyProfileDto(member);
    }

    public MyPageDto.MyItemDto getMyItems(Member member, String type) {
        if (!type.equalsIgnoreCase("title")  && !type.equalsIgnoreCase("icon")) throw new RestApiException(ErrorCode.ITEM_TYPE_NOT_FOUND);

        List<SaleStatus> saleStatus = List.of(SaleStatus.LIMITED_SALE, SaleStatus.ALWAYS_ON_SALE, SaleStatus.NOT_FOR_SALE);

        // 사용자 보유 아이템 조회
        List<AcquiredItem> items = acquiredItemRepository
                .findByMemberAndItem_Type(member, type=="title"?ItemType.TITLE:ItemType.ICON);

        List<List<MyPageDto.MyItemDetailDto>> result = new ArrayList<>();

        // saleStatus 순회하며 MyItemDetailDto 생성(칭호/타이틀 한번에 처리해서 코드가 구려짐)
        for(SaleStatus sale : saleStatus){
            List<MyPageDto.MyItemDetailDto> list = items.stream()
                    .filter(item -> item.getItem().getStatus().equals(sale))
                    .map(item -> item.getItem())
                    .map(item->MyPageConverter.toMyItemDetailDto(item,
                                    type=="title"
                                            ?((member.getProfileTitle() != null && member.getProfileTitle().getId() == item.getId())?true:false)
                                            :((member.getProfileIcon() != null && member.getProfileIcon().getId() == item.getId())?true:false)))
                    .collect(Collectors.toList());
            result.add(list);
        }

        // 위에서 생성한 list 이용해서 MyItemDto 생성
        return MyPageConverter.toMyItemDto(
                result.get(0)!=null?result.get(0):null,
                result.get(1)!=null?result.get(1):null,
                result.get(2)!=null?result.get(2):null);
    }

    public List<MyPageDto.MyBlockDto> getMyBlock(Member member) {
        List<Block> blockedMembers = blockRespotiroy.findByMember(member);
        return blockedMembers.stream()
                .map(members -> MyPageConverter.toMyBlockDto(members.getMember()))
                .collect(Collectors.toList());
    }

    public List<MyPageDto.MyGuestBookDto> getMyLikedBook(Member member) {
        List<LikedRally> rallyList = likedRallyRepository.findByMember(member);
        return rallyList.stream().map(dummy -> {
            List<VisitedPilgrimage> visited = visitedPilgrimageRepository
                    .findDistinctByMemberAndPilgrimage_Rally(member, dummy.getRally());
            return MyPageConverter.toMyGuestBookDto(dummy.getRally(), Long.valueOf(visited.size());
        });
    }

    // 성지순례 장소 중 중복 방문을 제외하고 방문한 장소의 횟수
    public MyPageDto.MyGuestBookDto getMyVisitedBook(Member member) {

        return null;
    }

    public MyPageDto.MyGuestBookDto getMyDoneBook(Member member) {
        return null;
    }
}
