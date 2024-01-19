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
import com.favoriteplace.global.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class RallyService {
    private final SecurityUtil securityUtil;
    private final RallyRepository rallyRepository;
    private final VisitedPilgrimageRepository visitedPilgrimageRepository;
    private final PilgrimageRepository pilgrimageRepository;

    @Transactional
    public HomeResponseDto.HomeRally getRecentRallyElseRandomRally(HttpServletRequest request) {
        Rally rally;
        long visitedCount = 0L;
        if(!securityUtil.isTokenExists(request)){
            rally = recommandRandomRally();
        }
        else{
            Long id = securityUtil.getUserFromHeader(request).getId();
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

    @Transactional
    public long getCompletePilgrimageCount(Long memberId, Long rallyId){
        List<Pilgrimage> pilgrimages = pilgrimageRepository.findByRallyId(rallyId);
        List<Long> pilgrimageIds = pilgrimages.stream().map(Pilgrimage::getId).toList();
        return visitedPilgrimageRepository.countByMemberIdAndPilgrimageIdIn(memberId, pilgrimageIds);
    }



}
