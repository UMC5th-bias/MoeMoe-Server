package com.favoriteplace.app.pilgrimage.service;

import com.favoriteplace.app.converter.CommonConverter;
import com.favoriteplace.app.item.converter.PointHistoryConverter;
import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.community.domain.GuestBook;
import com.favoriteplace.app.member.domain.enums.PointType;
import com.favoriteplace.app.domain.enums.RallyVersion;
import com.favoriteplace.app.item.domain.AcquiredItem;
import com.favoriteplace.app.domain.travel.CompleteRally;
import com.favoriteplace.app.domain.travel.LikedRally;
import com.favoriteplace.app.pilgrimage.domain.Pilgrimage;
import com.favoriteplace.app.domain.travel.Rally;
import com.favoriteplace.app.pilgrimage.domain.VisitedPilgrimage;
import com.favoriteplace.app.dto.CommonResponseDto;
import com.favoriteplace.app.pilgrimage.controller.dto.PilgrimageCertifyRequestDto;
import com.favoriteplace.app.pilgrimage.controller.dto.PilgrimageSocketDto;
import com.favoriteplace.app.item.repository.AcquiredItemRepository;
import com.favoriteplace.app.repository.CompleteRallyRepository;
import com.favoriteplace.app.community.repository.GuestBookRepository;
import com.favoriteplace.app.repository.LikedRallyRepository;
import com.favoriteplace.app.pilgrimage.repository.PilgrimageRepository;
import com.favoriteplace.app.item.repository.PointHistoryRepository;
import com.favoriteplace.app.repository.RallyRepository;
import com.favoriteplace.app.pilgrimage.repository.VisitedPilgrimageRepository;
import com.favoriteplace.app.notification.service.FCMNotificationService;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.websocket.RedisService;

import jakarta.persistence.EntityManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.favoriteplace.app.notification.service.FCMNotificationService.makeAnimationTopicName;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PilgrimageCommandService {
    private final GuestBookRepository guestBookRepository;
    private static final Double MAX_DISTANCE_WITHIN_100M = 0.00135;
    private final RallyRepository rallyRepository;
    private final PilgrimageRepository pilgrimageRepository;
    private final LikedRallyRepository likedRallyRepository;
    private final VisitedPilgrimageRepository visitedPilgrimageRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final CompleteRallyRepository completeRallyRepository;
    private final AcquiredItemRepository acquiredItemRepository;
    private final FCMNotificationService fcmNotificationService;
    private final EntityManager em;
    private final RedisService redisService;
    private Map<Long, Map<Long, PilgrimageSocketDto.ButtonState>> lastButtonStateCache = new ConcurrentHashMap<>();

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

    /***
     * 성지순례 방문 인증하기
     * @param pilgrimageId 성지순례 아이디
     * @param member 인증한 사용자
     * @return
     */
    public CommonResponseDto.RallyResponseDto certifyToPilgrimage(Long pilgrimageId, Member member) {
        Pilgrimage pilgrimage = pilgrimageRepository.findById(pilgrimageId).orElseThrow(
                () -> new RestApiException(ErrorCode.PILGRIMAGE_NOT_FOUND));

        List<VisitedPilgrimage> visitedPilgrimages = visitedPilgrimageRepository
                .findByPilgrimageAndMemberOrderByCreatedAtDesc(pilgrimage, member);

        ZoneId serverZoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime nowInServerTimeZone = ZonedDateTime.now(serverZoneId);

        if (visitedPilgrimages.isEmpty()
                || (!visitedPilgrimages.isEmpty()
                && visitedPilgrimages.get(0).getPilgrimage().getCreatedAt().atZone(serverZoneId).plusHours(24L)
                .isBefore(nowInServerTimeZone))) {

            // 성공 시 포인트 지급 -> 15p & visitedPilgrimage 추가
            successVisitedAndPointProcess(member, pilgrimage);

            Long completeCount = visitedPilgrimageRepository.findByDistinctCount(member.getId(),
                    pilgrimage.getRally().getId());
            log.info("completeCount=" + completeCount);

            // 랠리를 완료했는지 확인
            if (checkCompleteRally(member, pilgrimage, completeCount)) {
                return CommonConverter.toRallyResponseDto(true, true,
                        "<" + pilgrimage.getRally().getItem().getName() + "> 칭호를 얻었습니다!");
            }
        } else {
            throw new RestApiException(ErrorCode.PILGRIMAGE_ALREADY_CERTIFIED);
        }
        return CommonConverter.toRallyResponseDto(true, false, "성지순례 인증하기 15P를 얻으셨습니다!");
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

    public boolean isUserAtPilgrimage(Pilgrimage pilgrimage, Double latitude, Double longitude) {
        return (pilgrimage.getLatitude() + MAX_DISTANCE_WITHIN_100M >= latitude
                && pilgrimage.getLatitude() - MAX_DISTANCE_WITHIN_100M <= latitude) &&
                (pilgrimage.getLongitude() + MAX_DISTANCE_WITHIN_100M >= longitude
                        && pilgrimage.getLongitude() - MAX_DISTANCE_WITHIN_100M <= longitude);
    }

    /**
     * WebSocket location 이벤트
     *
     * @param pilgrimageId
     * @param userLocation
     * @param member
     * @return
     */
    public PilgrimageSocketDto.ButtonState buttonStatusUpdate(Long pilgrimageId,
                                                              PilgrimageCertifyRequestDto userLocation, Member member) {
        Pilgrimage pilgrimage = pilgrimageRepository.findById(pilgrimageId)
                .orElseThrow(() -> new RestApiException(ErrorCode.PILGRIMAGE_NOT_FOUND));

        // 위치 정보 바탕으로 인증 가능 여부 Redis 저장
        isLocationVerified(member, pilgrimage, userLocation.latitude(), userLocation.longitude());

        // 이전 버튼 상태와 비교해서 달라졌다면 전송, 아니면 null
        synchronized (this) {
            lastButtonStateCache.putIfAbsent(member.getId(), new ConcurrentHashMap<>());
            Map<Long, PilgrimageSocketDto.ButtonState> pilgrimageStateMap = lastButtonStateCache.get(member.getId());

            if (pilgrimageStateMap == null) {
                pilgrimageStateMap = new ConcurrentHashMap<>();
                lastButtonStateCache.put(member.getId(), pilgrimageStateMap);
            }

            PilgrimageSocketDto.ButtonState lastState = pilgrimageStateMap.get(pilgrimageId);

            // 버튼 상태 업데이트
            PilgrimageSocketDto.ButtonState buttonState = determineButtonState(member, pilgrimageId);

            if (!buttonState.equals(lastState)) {
                pilgrimageStateMap.put(pilgrimageId, buttonState);
                return buttonState;
            }
        }
        return null;
    }

    /**
     * 사용자가 성지순례 위치 100M 이내에 존재하는지 확인, 맞다면 redis 저장
     *
     * @param pilgrimage 성지순례
     * @param latitude   사용자의 위도
     * @param longitude  사용자의 경도
     */
    public void isLocationVerified(Member member, Pilgrimage pilgrimage, Double latitude, Double longitude) {
        if (isUserAtPilgrimage(pilgrimage, latitude, longitude)) {
            redisService.saveCertificationTime(member.getId(), pilgrimage.getId());
        }
    }

    /**
     * 웹소켓 버튼 상태 지정
     *
     * @param member       사용자
     * @param pilgrimageId 성지순례 ID
     * @return
     */
    public PilgrimageSocketDto.ButtonState determineButtonState(Member member, Long pilgrimageId) {
        PilgrimageSocketDto.ButtonState newState = new PilgrimageSocketDto.ButtonState();
        newState.setCertifyButtonEnabled(false);
        newState.setGuestbookButtonEnabled(false);
        newState.setMultiGuestbookButtonEnabled(false);

        // 캐시에 저장된 버튼이 없다면 새로 상태 저장
        Pilgrimage pilgrimage = pilgrimageRepository.findById(pilgrimageId)
                .orElseThrow(() -> new RestApiException(ErrorCode.PILGRIMAGE_NOT_FOUND));

        // 사용자가 지난 24시간 내에 인증 버튼 눌렀는지 확인
        boolean certifiedInLast = checkIfCertifiedInLast24Hours(member, pilgrimage);
        // 사용자가 이번 인증하기에 이미 방명록을 작성했는지 확인 (모든 상호작용 완료했는지)
        boolean hasWrittenGuestbook = checkIfGuestbookWritten(member, pilgrimage);

        // 24시간 내 인증 기록이 있는가?
        if (!certifiedInLast) {
            boolean isCertificationExpired = redisService.isCertificationExpired(member, pilgrimage);
            newState.setCertifyButtonEnabled((!isCertificationExpired) ? true : false);
        }
        // 이번 인증 기록에 대한 방명록이 있는가?
        else if (certifiedInLast && !hasWrittenGuestbook) {
            boolean hasMultiWrittenGuestbook = checkIfMultiGuestbookWritten(member, pilgrimage);
            newState.setGuestbookButtonEnabled(hasMultiWrittenGuestbook ? false : true);
            newState.setMultiGuestbookButtonEnabled(hasMultiWrittenGuestbook ? true : false);
        }
        synchronized (this) {
            lastButtonStateCache.get(member.getId()).put(pilgrimageId, newState);
        }
        return newState;
    }

    /**
     * @return
     */
    public PilgrimageSocketDto.ButtonState initButton(Member member, Long pilgrimageId) {
        PilgrimageSocketDto.ButtonState newState = new PilgrimageSocketDto.ButtonState();
        newState.setCertifyButtonEnabled(false);
        newState.setGuestbookButtonEnabled(false);
        newState.setMultiGuestbookButtonEnabled(false);

        // 이미 캐시에 저장된 버튼이 있다면 바로 호출
        synchronized (this) {
            lastButtonStateCache.putIfAbsent(member.getId(), new ConcurrentHashMap<>());
            Map<Long, PilgrimageSocketDto.ButtonState> pilgrimageStateMap = lastButtonStateCache.get(member.getId());

            PilgrimageSocketDto.ButtonState lastState = pilgrimageStateMap.get(pilgrimageId);

            if (lastState != null) {
                pilgrimageStateMap.put(pilgrimageId, newState);
                return lastState;
            }
            return newState;
        }
    }

    /**
     * 24시간 이내 인증 기록이 있는지 확인
     *
     * @param member
     * @param pilgrimage
     * @return
     */
    private boolean checkIfCertifiedInLast24Hours(Member member, Pilgrimage pilgrimage) {
        List<VisitedPilgrimage> visitedPilgrimages = visitedPilgrimageRepository
                .findByPilgrimageAndMemberOrderByCreatedAtDesc(pilgrimage, member);

        if (visitedPilgrimages.isEmpty()) {
            return false;
        }

        VisitedPilgrimage lastVisited = visitedPilgrimages.get(0);
        ZonedDateTime lastVisitedTime = lastVisited.getCreatedAt().atZone(ZoneId.of("Asia/Seoul"));

        ZonedDateTime nowInServerTimeZone = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

        return lastVisitedTime.isAfter(nowInServerTimeZone.minusHours(24));
    }

    /**
     * 방명록 작성 여부 확인
     *
     * @param member
     * @param pilgrimage
     * @return
     */
    private boolean checkIfGuestbookWritten(Member member, Pilgrimage pilgrimage) {
        // 방명록 목록 조회
        List<GuestBook> guestBookList = guestBookRepository.findByMemberAndPilgrimageOrderByCreatedAtDesc(member,
                pilgrimage);
        if (guestBookList == null || guestBookList.isEmpty()) {
            return false;
        }

        // 방문 인증 정보 조회
        List<VisitedPilgrimage> visitedPilgrimages = visitedPilgrimageRepository
                .findByPilgrimageAndMemberOrderByCreatedAtDesc(pilgrimage, member);

        if (visitedPilgrimages.isEmpty()) {
            return false;
        }

        VisitedPilgrimage lastVisited = visitedPilgrimages.get(0);
        ZonedDateTime lastVisitedTime = lastVisited.getCreatedAt().atZone(ZoneId.of("Asia/Seoul"));

        // 최근 방문 인증 정보에 대한 방명록이 존재하는지 확인
        for (GuestBook guestBook : guestBookList) {
            ZonedDateTime guestBookCreatedTime = guestBook.getCreatedAt().atZone(ZoneId.of("Asia/Seoul"));

            if (guestBookCreatedTime.isAfter(lastVisitedTime)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 방명록 다회 작성 여부 확인
     *
     * @param member
     * @param pilgrimage
     * @return
     */
    private boolean checkIfMultiGuestbookWritten(Member member, Pilgrimage pilgrimage) {
        List<GuestBook> guestBookList = guestBookRepository.findByMemberAndPilgrimageOrderByCreatedAtDesc(member,
                pilgrimage);
        if (guestBookList == null || guestBookList.isEmpty()) {
            return false;
        }
        return true;
    }

    private boolean checkCoordinate(PilgrimageCertifyRequestDto form, Pilgrimage pilgrimage) {
        return pilgrimage.getLatitude() + MAX_DISTANCE_WITHIN_100M >= form.latitude()
                && pilgrimage.getLatitude() - MAX_DISTANCE_WITHIN_100M <= form.latitude()
                && pilgrimage.getLongitude() + MAX_DISTANCE_WITHIN_100M >= form.longitude()
                && pilgrimage.getLongitude() - MAX_DISTANCE_WITHIN_100M <= form.longitude();
    }

    private void successVisitedAndPointProcess(Member member, Pilgrimage pilgrimage) {
        VisitedPilgrimage newVisited = VisitedPilgrimage.builder().pilgrimage(pilgrimage).member(member).build();
        log.info("visited=" + newVisited.getId());
        visitedPilgrimageRepository.save(newVisited);
        pointHistoryRepository.save(PointHistoryConverter.toPointHistory(member, 15L, PointType.ACQUIRE));
        member.updatePoint(15L);
        log.info("clear");
    }

    private boolean checkCompleteRally(Member member, Pilgrimage pilgrimage, Long completeCount) {
        if (completeCount == pilgrimage.getRally().getPilgrimageNumber()) {
            List<CompleteRally> completeRally = completeRallyRepository.findByMemberAndRally(member,
                    pilgrimage.getRally());
            log.info("size=" + completeRally.size());
            // 이전에 이미 랠리를 완료한 상태인지 확인
            if (completeRally.isEmpty()) {
                // 최초 완료에 한해 칭호 획득
                if (completeRally == null) {
                    acquiredItemRepository.save(
                            AcquiredItem.builder().item(pilgrimage.getRally().getItem()).member(member).build());
                }
                // 포인트 획득
                pointHistoryRepository.save(PointHistoryConverter.toPointHistory(member, 100L, PointType.ACQUIRE));
                // 완료 랠리 추가
                completeRallyRepository.save(
                        CompleteRally.builder().rally(pilgrimage.getRally()).member(member).version(RallyVersion.v1)
                                .build());
                // 랠리 완료자 목록에 추가
                pilgrimage.getRally().addAchieveNumber();
                rallyRepository.save(pilgrimage.getRally());
                return true;
            }
        }
        return false;
    }
}
