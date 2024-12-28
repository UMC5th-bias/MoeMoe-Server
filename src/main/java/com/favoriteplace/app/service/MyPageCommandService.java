package com.favoriteplace.app.service;

import com.favoriteplace.app.converter.CommonConverter;
import com.favoriteplace.app.domain.Block;
import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.enums.ItemType;
import com.favoriteplace.app.domain.item.Item;
import com.favoriteplace.app.dto.CommonResponseDto;
import com.favoriteplace.app.dto.MyPageDto;
import com.favoriteplace.app.dto.MyPageDto.MyFcmTokenDto;
import com.favoriteplace.app.repository.AcquiredItemRepository;
import com.favoriteplace.app.repository.BlockRepository;
import com.favoriteplace.app.repository.ItemRepository;
import com.favoriteplace.app.repository.MemberRepository;
import com.favoriteplace.app.service.fcm.FCMNotificationService;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageCommandService {
    private final BlockRepository blockRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final AcquiredItemRepository acquiredItemRepository;
    private final FCMNotificationService fcmNotificationService;

    /**
     * 다른 유저를 차단 또는 차단 해제
     *
     * @param member          : 앱을 현재 사용하고 있는 사용자
     * @param blockedMemberId : 차단하고 싶은 또는 차단 취소하고 싶은 유저
     * @return
     */
    @Transactional
    public MyPageDto.MyModifyBlockDto modifyMemberBlock(Member member, Long blockedMemberId) {
        Member blockedMember = memberRepository.findById(blockedMemberId)
                .orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_FOUND));
        if (member.equals(blockedMember)) {
            throw new RestApiException(ErrorCode.CANT_BLOCK_SELF);
        }
        Boolean isExist = blockRepository.existsByMemberIdAndBlockedMemberId(member.getId(), blockedMember.getId());
        boolean isBlocked;
        if (isExist) {
            //차단 해제
            blockRepository.deleteByMemberIdAndBlockedMemberId(member.getId(), blockedMember.getId());
            isBlocked = false;
        } else {
            //차단
            Block block = Block.builder().member(member).blockedMember(blockedMember).build();
            blockRepository.save(block);
            isBlocked = true;
        }
        return MyPageDto.MyModifyBlockDto.builder().isBlocked(isBlocked).build();
    }

    /**
     * 보유하는 아이템 착용하기
     *
     * @param itemId 착용하려는 아이템
     * @param member 착용자
     * @return
     */
    @Transactional
    public CommonResponseDto.PostResponseDto wearItem(Long itemId, Member member) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RestApiException(ErrorCode.ITEM_NOT_EXISTS));
        acquiredItemRepository.findByMemberAndItem(member, item)
                .orElseThrow(() -> new RestApiException(ErrorCode.ITEM_NOT_ACQUIRED));
        if (item.getType() == ItemType.ICON) {
            member.updateIcon(item);
        } else if (item.getType() == ItemType.TITLE) {
            member.updateTitle(item);
        }
        memberRepository.save(member);
        return CommonConverter.toPostResponseDto(true, "착용이 완료되었습니다.");
    }

    /**
     * FCM 토큰 등록 & 변경
     *
     * @param member  사용자
     * @param request RequestBody
     */
    @Transactional
    public void modifyFcmToken(Member member, MyFcmTokenDto request) {
        fcmNotificationService.refreshFCMTopicAndToken(member, request.getFcmToken());
    }
}
