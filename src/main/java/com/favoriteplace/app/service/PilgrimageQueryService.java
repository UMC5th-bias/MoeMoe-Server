package com.favoriteplace.app.service;

import com.favoriteplace.app.converter.RallyConverter;
import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.travel.LikedRally;
import com.favoriteplace.app.domain.travel.Pilgrimage;
import com.favoriteplace.app.domain.travel.Rally;
import com.favoriteplace.app.domain.travel.VisitedPilgrimage;
import com.favoriteplace.app.dto.travel.PilgrimageDto;
import com.favoriteplace.app.dto.travel.RallyDto;
import com.favoriteplace.app.repository.*;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.ErrorResponse;
import com.favoriteplace.global.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PilgrimageQueryService {
    private final PilgrimageRepository pilgrimageRepository;
    private final RallyRepository rallyRepository;
    private final MemberRepository memberRepository;
    private final LikedRallyRepository likedRallyRepository;
    private final VisitedPilgrimageRepository visitedPilgrimageRepository;

    public RallyDto.RallyDetailResponseDto getRallyDetail(Long rallyId) {
        Rally rally = rallyRepository.findById(rallyId).orElseThrow(
                ()-> new RestApiException(ErrorCode.RALLY_NOT_FOUND));
        Member member = memberRepository.findById(0L).orElse(null);
        // 비회원
        if (member == null){
            return RallyConverter.toRallyDetailResponseDto(rally, 0L, false, false);
        }
        LikedRally isLikeList = likedRallyRepository.findByRallyAndMember(rally, member);
        List<VisitedPilgrimage> pilgrimageNumber = visitedPilgrimageRepository.findByMemberAndPilgrimage_Rally(member, rally);
        if (isLikeList == null) {
            return RallyConverter.toRallyDetailResponseDto(rally, Long.valueOf(pilgrimageNumber.size()), false, true);
        }
        return RallyConverter.toRallyDetailResponseDto(rally, Long.valueOf(pilgrimageNumber.size()), true, true);
    }

    // 사용자 정보 없을 때 RallyAddressPilgrimageDto.isVisited->false
    public RallyDto.RallyAddressListDto getRallyAddressList(Long rallyId) {
        Rally rally = rallyRepository.findById(rallyId).orElseThrow(
                ()-> new RestApiException(ErrorCode.RALLY_NOT_FOUND));
        Member member = memberRepository.findById(0L).orElse(null);
        // 비회원
        if (member == null){

        } // 회원
        else {

        }
        return null;
    }

    public PilgrimageDto.PilgrimageDetailDto getPilgrimageDetail(Long pilgrimageId) {
        Pilgrimage pilgrimage = pilgrimageRepository.findById(pilgrimageId)
                .orElseThrow(() -> new RestApiException(ErrorCode.PILGRIMAGE_NOT_FOUND));
//        회원정보 조회
//        Member member = memberRepository.findByAccessToken()
//                .orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_FOUND));

        return null;
    }
}
