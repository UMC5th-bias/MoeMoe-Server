package com.favoriteplace.app.service;

import com.favoriteplace.app.domain.travel.Pilgrimage;
import com.favoriteplace.app.domain.travel.Rally;
import com.favoriteplace.app.domain.travel.VisitedPilgrimage;
import com.favoriteplace.app.dto.HomeResponseDto;
import com.favoriteplace.app.repository.MemberRepository;
import com.favoriteplace.app.repository.PilgrimageRepository;
import com.favoriteplace.app.repository.RallyRepository;
import com.favoriteplace.app.repository.VisitedPilgrimageRepository;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RallyService {
    private final MemberRepository memberRepository;
    private final RallyRepository rallyRepository;
    private final VisitedPilgrimageRepository visitedPilgrimageRepository;
    private final PilgrimageRepository pilgrimageRepository;

    @Transactional
    public HomeResponseDto.HomeRally getMemberRecentRally(String accessToken) {
        /*
        [JWT]
        String userId = jwtUtil.getUserIdFromToken(accessToken);
        Member member = memberRepository.findById(Long.valueOf(accessToken))
                .orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_FOUND));
         */
        //[임시] : accessToken 대신 memberId 사용
        memberRepository.findById(Long.valueOf(accessToken))
                .orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_FOUND));

        List<VisitedPilgrimage> visitedPilgrimages = visitedPilgrimageRepository.findByMemberIdOrderByModifiedAtDesc(Long.valueOf(accessToken));

        Rally rally;
        long visitedCount = 0L;
        if(visitedPilgrimages.isEmpty()){
            //랜덤 랠리
            rally = recommandRandomRally();
        }else{
            rally = visitedPilgrimages.get(0).getPilgrimage().getRally();
            visitedCount = getCompletePilgrimageCount(Long.valueOf(accessToken), rally.getId());
        }
        return HomeResponseDto.HomeRally.of(rally, visitedCount);
    }

    @Transactional
    public HomeResponseDto.HomeRally getRandomRally() {
        Rally rally = recommandRandomRally();
        return HomeResponseDto.HomeRally.of(rally, 0L);
    }

    @Transactional
    public long getCompletePilgrimageCount(Long memberId, Long rallyId){
        List<Pilgrimage> pilgrimages = pilgrimageRepository.findByRallyId(rallyId);
        List<Long> pilgrimageIds = pilgrimages.stream().map(Pilgrimage::getId).toList();
        return visitedPilgrimageRepository.countByMemberIdAndPilgrimageIdIn(memberId, pilgrimageIds);
    }

    @Transactional
    public Rally recommandRandomRally(){
        List<Rally> rallies = rallyRepository.findAll();
        if(rallies.isEmpty()){
            throw new RestApiException(ErrorCode.RALLY_NOT_FOUND);
        }
        Random random = new Random();
        int index = random.nextInt(rallies.size());
        return rallies.get(index);
    }

}
