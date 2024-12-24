package com.favoriteplace.app.service;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.travel.Pilgrimage;
import com.favoriteplace.app.domain.travel.Rally;
import com.favoriteplace.app.domain.travel.VisitedPilgrimage;
import com.favoriteplace.app.dto.HomeResponseDto;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RallyService {
    private final RallyRepository rallyRepository;
    private final VisitedPilgrimageRepository visitedPilgrimageRepository;
    private final PilgrimageRepository pilgrimageRepository;

    public HomeResponseDto.HomeRally getRecentRallyElseRandomRally(Boolean isLoggedIn, Member member) {
        Rally rally;
        long visitedCount = 0L;
        if(!isLoggedIn){
            rally = recommandRandomRally();
        }
        else{
            Long id = member.getId();
            List<VisitedPilgrimage> visitedPilgrimages = visitedPilgrimageRepository.findByMemberIdOrderByModifiedAtDesc(id);
            if(visitedPilgrimages.isEmpty()){
                //랜덤 랠리
                rally = recommandRandomRally();
            }else{
                rally = visitedPilgrimages.get(0).getPilgrimage().getRally();
                visitedCount = getCompletePilgrimageCount(id, rally.getId());
            }
        }
        return HomeResponseDto.HomeRally.of(rally, visitedCount);
    }

    public Rally recommandRandomRally(){
        List<Rally> rallies = rallyRepository.findAll();
        if(rallies.isEmpty()){
            throw new RestApiException(ErrorCode.RALLY_NOT_FOUND);
        }
        Random random = new Random();
        int index = random.nextInt(rallies.size());
        return rallies.get(index);
    }

    public long getCompletePilgrimageCount(Long memberId, Long rallyId){
        List<Pilgrimage> pilgrimages = pilgrimageRepository.findByRallyId(rallyId);
        return visitedPilgrimageRepository.findByDistinctCount(memberId, rallyId);
    }

}
