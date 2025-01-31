package com.favoriteplace.app.rally.service;

import static com.favoriteplace.app.notification.service.FCMNotificationService.makeAnimationTopicName;

import com.favoriteplace.app.common.converter.CommonConverter;
import com.favoriteplace.app.common.dto.CommonResponseDto;
import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.notification.service.FCMNotificationService;
import com.favoriteplace.app.pilgrimage.domain.Pilgrimage;
import com.favoriteplace.app.rally.domain.LikedRally;
import com.favoriteplace.app.rally.domain.Rally;
import com.favoriteplace.app.pilgrimage.domain.VisitedPilgrimage;
import com.favoriteplace.app.community.controller.dto.HomeResponseDto;
import com.favoriteplace.app.pilgrimage.repository.PilgrimageRepository;
import com.favoriteplace.app.rally.repository.LikedRallyRepository;
import com.favoriteplace.app.rally.repository.RallyRepository;
import com.favoriteplace.app.pilgrimage.repository.VisitedPilgrimageRepository;
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
    private final FCMNotificationService fcmNotificationService;
    private final LikedRallyRepository likedRallyRepository;

    /***
     * 랠리 찜하기
     * @param rallyId 랠리 아이디
     * @param member 찜한 사용자
     * @return
     */
    public CommonResponseDto.PostResponseDto likeToRally(Long rallyId, Member member) {
        Rally rally = rallyRepository.findById(rallyId).orElseThrow(
                () -> new RestApiException(ErrorCode.RALLY_NOT_FOUND));
        LikedRally likedRally = likedRallyRepository.findByRallyAndMember(rally, member);

        if (likedRally == null) {
            LikedRally newLikedRally = LikedRally.builder().rally(rally).member(member).build();
            likedRallyRepository.save(newLikedRally);
            return CommonConverter.toPostResponseDto(true, "찜 목록에 추가됐습니다.");
        } else {
            likedRallyRepository.delete(likedRally);
            return CommonConverter.toPostResponseDto(false, "찜을 취소했습니다.");
        }
    }

    /**
     * 랠리 구독 (FCM 토픽에 해당 사용자의 토큰 추가)
     */
    public void subscribeRally(Long rallyId, Member member) {
        if (member.getFcmToken() == null) {
            throw new RestApiException(ErrorCode.FCM_TOKEN_NOT_FOUND);
        }
        fcmNotificationService.subscribeTopic(makeAnimationTopicName(rallyId), member.getFcmToken());
    }

    /**
     * 랠리 구독 취소 (FCM 토픽에 해당 사용자의 토큰 제거)
     */
    public void unsubscribeRally(Long rallyId, Member member) {
        if (member.getFcmToken() == null) {
            throw new RestApiException(ErrorCode.FCM_TOKEN_NOT_FOUND);
        }
        fcmNotificationService.unsubscribeTopic(makeAnimationTopicName(rallyId), member.getFcmToken());
    }

    public HomeResponseDto.HomeRally getRecentRallyElseRandomRally(Boolean isLoggedIn, Member member) {
        Rally rally;
        long visitedCount = 0L;
        if (!isLoggedIn) {
            rally = recommandRandomRally();
        } else {
            Long id = member.getId();
            List<VisitedPilgrimage> visitedPilgrimages = visitedPilgrimageRepository.findByMemberIdOrderByModifiedAtDesc(
                    id);
            if (visitedPilgrimages.isEmpty()) {
                //랜덤 랠리
                rally = recommandRandomRally();
            } else {
                rally = visitedPilgrimages.get(0).getPilgrimage().getRally();
                visitedCount = getCompletePilgrimageCount(id, rally.getId());
            }
        }
        return HomeResponseDto.HomeRally.of(rally, visitedCount);
    }

    public Rally recommandRandomRally() {
        List<Rally> rallies = rallyRepository.findAll();
        if (rallies.isEmpty()) {
            throw new RestApiException(ErrorCode.RALLY_NOT_FOUND);
        }
        Random random = new Random();
        int index = random.nextInt(rallies.size());
        return rallies.get(index);
    }

    public long getCompletePilgrimageCount(Long memberId, Long rallyId) {
        List<Pilgrimage> pilgrimages = pilgrimageRepository.findByRallyId(rallyId);
        return visitedPilgrimageRepository.findByDistinctCount(memberId, rallyId);
    }

}
