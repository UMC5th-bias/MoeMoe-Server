package com.favoriteplace.app.service;

import com.favoriteplace.app.domain.Block;
import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.dto.MyPageDto;
import com.favoriteplace.app.repository.BlockRepository;
import com.favoriteplace.app.repository.MemberRepository;
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

    /**
     * 다른 유저를 차단 또는 차단 해제
     * @param member : 앱을 현재 사용하고 있는 사용자
     * @param blockedMemberId : 차단하고 싶은 또는 차단 취소하고 싶은 유저
     * @return
     */
    @Transactional
    public MyPageDto.MyModifyBlockDto modifyMemberBlock(Member member, Long blockedMemberId) {
        Member blockedMember = memberRepository.findById(blockedMemberId).orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_FOUND));
        if(member.equals(blockedMember)){throw new RestApiException(ErrorCode.CANT_BLOCK_SELF);}
        Boolean isExist = blockRepository.existsByMemberIdAndBlockedMemberId(member.getId(), blockedMember.getId());
        boolean isBlocked;
        if(isExist){
            //차단 해제
            blockRepository.deleteByMemberIdAndBlockedMemberId(member.getId(), blockedMember.getId());
            isBlocked = false;
        }else{
            //차단
            Block block = Block.builder().member(member).blockedMember(blockedMember).build();
            blockRepository.save(block);
            isBlocked = true;
        }
        return MyPageDto.MyModifyBlockDto.builder().isBlocked(isBlocked).build();
    }

}
