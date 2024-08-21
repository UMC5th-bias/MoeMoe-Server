package com.favoriteplace.app.service;

import com.favoriteplace.app.converter.CommonConverter;
import com.favoriteplace.app.converter.PointHistoryConverter;
import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.enums.PointType;
import com.favoriteplace.app.domain.enums.RallyVersion;
import com.favoriteplace.app.domain.item.AcquiredItem;
import com.favoriteplace.app.domain.item.PointHistory;
import com.favoriteplace.app.domain.travel.*;
import com.favoriteplace.app.dto.CommonResponseDto;
import com.favoriteplace.app.dto.travel.PilgrimageDto;
import com.favoriteplace.app.dto.travel.PilgrimageSocketDto;
import com.favoriteplace.app.repository.*;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.websocket.RedisService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PilgrimageCommandService {
    private final MemberRepository memberRepository;
    private final RallyRepository rallyRepository;
    private final PilgrimageRepository pilgrimageRepository;
    private final LikedRallyRepository likedRallyRepository;
    private final VisitedPilgrimageRepository visitedPilgrimageRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final CompleteRallyRepository completeRallyRepository;
    private final AcquiredItemRepository acquiredItemRepository;
    private final RedisService redisService;

    /***
     * 랠리 찜하기
     * @param rallyId 랠리 아이디
     * @param member 찜한 사용자
     * @return
     */
    public CommonResponseDto.PostResponseDto likeToRally(Long rallyId, Member member) {
        Rally rally = rallyRepository.findById(rallyId).orElseThrow(
                ()->new RestApiException(ErrorCode.RALLY_NOT_FOUND));
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
    public CommonResponseDto.RallyResponseDto certifyToPilgrimage(Long pilgrimageId,
                                                                 Member member,
                                                                 PilgrimageDto.PilgrimageCertifyRequestDto form) {
        Pilgrimage pilgrimage = pilgrimageRepository.findById(pilgrimageId).orElseThrow(
                () -> new RestApiException(ErrorCode.PILGRIMAGE_NOT_FOUND));

        List<VisitedPilgrimage> visitedPilgrimages = visitedPilgrimageRepository
                .findByPilgrimageAndMemberOrderByCreatedAtDesc(pilgrimage, member);

        ZoneId serverZoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime nowInServerTimeZone = ZonedDateTime.now(serverZoneId);

//        log.info("now="+nowInServerTimeZone);
//        if (!visitedPilgrimages.isEmpty()) {
//            log.info("pilgrimage="+visitedPilgrimages.get(0).getPilgrimage().getCreatedAt().atZone(serverZoneId));
//        }

        if (visitedPilgrimages.isEmpty()
                || (!visitedPilgrimages.isEmpty()
                && visitedPilgrimages.get(0).getPilgrimage().getCreatedAt().atZone(serverZoneId).plusHours(24L).isBefore(nowInServerTimeZone))) {
            // 현재 좌표가 성지순례 장소 좌표 기준 +-0.00135 이내인지 확인
            if (checkCoordinate(form, pilgrimage)){
                throw new RestApiException(ErrorCode.PILGRIMAGE_CAN_NOT_CERTIFIED);
            }
            // 성공 시 포인트 지급 -> 15p & visitedPilgrimage 추가
            successVisitedAndPointProcess(member, pilgrimage);

            Long completeCount = visitedPilgrimageRepository.findByDistinctCount(member.getId(), pilgrimage.getRally().getId());
            log.info("completeCount="+completeCount);
//            // 랠리를 완료했는지 확인
            if (checkCompleteRally(member, pilgrimage, completeCount))
                return CommonConverter.toRallyResponseDto(true, true,"<"+pilgrimage.getRally().getItem().getName()+"> 칭호를 얻었습니다!");
        } else
            throw new RestApiException(ErrorCode.PILGRIMAGE_ALREADY_CERTIFIED);
        return CommonConverter.toRallyResponseDto(true, false,"성지순례 인증하기 15P를 얻으셨습니다!");
    }

    private boolean checkCompleteRally(Member member, Pilgrimage pilgrimage, Long completeCount) {
        if (completeCount == pilgrimage.getRally().getPilgrimageNumber()) {
            List<CompleteRally> completeRally = completeRallyRepository.findByMemberAndRally(member, pilgrimage.getRally());
            log.info("size="+completeRally.size());
            // 이전에 이미 랠리를 완료한 상태인지 확인
            if (completeRally.isEmpty()) {
                // 최초 완료에 한해 칭호 획득
                if (completeRally == null)
                    acquiredItemRepository.save(AcquiredItem.builder().item(pilgrimage.getRally().getItem()).member(member).build());
                // 포인트 획득
                pointHistoryRepository.save(PointHistoryConverter.toPointHistory(member, 100L, PointType.ACQUIRE));
                // 완료 랠리 추가
                completeRallyRepository.save(CompleteRally.builder().rally(pilgrimage.getRally()).member(member).version(RallyVersion.v1).build());
                // 랠리 완료자 목록에 추가
                pilgrimage.getRally().addAchieveNumber();
                rallyRepository.save(pilgrimage.getRally());
                return true;
            }
        }
        return false;
    }

    private boolean checkCoordinate(PilgrimageDto.PilgrimageCertifyRequestDto form, Pilgrimage pilgrimage) {
        return pilgrimage.getLatitude() + 0.00135 >= form.getLatitude() && pilgrimage.getLatitude() - 0.00135 <= form.getLatitude()
                && pilgrimage.getLongitude() + 0.00135 >= form.getLongitude() && pilgrimage.getLongitude() - 0.00135 <= form.getLongitude();
    }

    private void successVisitedAndPointProcess(Member member, Pilgrimage pilgrimage) {
        VisitedPilgrimage newVisited = VisitedPilgrimage.builder().pilgrimage(pilgrimage).member(member).build();
        log.info("visited="+newVisited.getId());
        visitedPilgrimageRepository.save(newVisited);
        pointHistoryRepository.save(PointHistoryConverter.toPointHistory(member, 15L, PointType.ACQUIRE));
        member.updatePoint(15L);
        log.info("clear");
    }

    public boolean isUserAtPilgrimage(Pilgrimage pilgrimage, Double latitude, Double longitude) {
        return (pilgrimage.getLatitude() + 0.00135 >= latitude && pilgrimage.getLatitude() - 0.00135 <= latitude) &&
                (pilgrimage.getLongitude() + 0.00135 >= longitude && pilgrimage.getLongitude() - 0.00135 <= longitude);
    }

    /**
     * 웹소켓 버튼 상태 이벤트
     * @param memberId
     * @param pilgrimageId
     * @param latitude
     * @param longitude
     * @return
     */
    public PilgrimageSocketDto.ButtonState determineButtonState(Long memberId,
                                                                Long pilgrimageId,
                                                                double latitude, double longitude) {
        Pilgrimage pilgrimage = pilgrimageRepository.findById(pilgrimageId)
                .orElseThrow(() -> new RestApiException(ErrorCode.PILGRIMAGE_NOT_FOUND));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(()->new RestApiException(ErrorCode.USER_NOT_FOUND));

        // 사용자가 인증 장소에 있는지 확인
        boolean nearPilgrimage = isUserAtPilgrimage(pilgrimage, latitude, longitude);
        // redis에 사용자의 인증 기록이 남아있는지 확인 (24시간 이후 만료됨)
        boolean isCertificationExpired = redisService.isCertificationExpired(member, pilgrimage);
        // 사용자가 지난 24시간 내에 인증 버튼 눌렀는지 확인
        boolean certifiedInLast24Hours = checkIfCertifiedInLast24Hours(member, pilgrimage);
        // 사용자가 이번 인증하기에 이미 방명록을 작성했는지 확인
        boolean hasWrittenGuestbook = checkIfGuestbookWritten(member, pilgrimage);
        // 사용자가 24시간 전에 작성한 방명록이 1개 이상인지 확인
        boolean hasMultiWrittenGuestbook = checkIfMultiGuestbookWritten(member, pilgrimage);

        PilgrimageSocketDto.ButtonState newState = new PilgrimageSocketDto.ButtonState();
        if (nearPilgrimage && !certifiedInLast24Hours) {
            newState.setCertifyButtonEnabled(true);  // 인증하기 버튼 활성화
            newState.setGuestbookButtonEnabled(false); // 방명록 쓰기 버튼 비활성화
        }
        else if (certifiedInLast24Hours && !hasWrittenGuestbook) {
            newState.setCertifyButtonEnabled(false);  // 인증하기 버튼 비활성화
            newState.setGuestbookButtonEnabled(true); // 방명록 쓰기 버튼 활성화
        }
        else {
            newState.setCertifyButtonEnabled(false);  // 인증하기 버튼 비활성화
            newState.setGuestbookButtonEnabled(false); // 방명록 쓰기 버튼 비활성화
        }
        return newState;
    }

    private boolean checkIfCertifiedInLast24Hours(Member member, Pilgrimage pilgrimage) {
        // 인증 버튼을 누른 시간 확인
        List<VisitedPilgrimage> visitedPilgrimages = visitedPilgrimageRepository
                .findByPilgrimageAndMemberOrderByCreatedAtDesc(pilgrimage, member);

        ZoneId serverZoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime nowInServerTimeZone = ZonedDateTime.now(serverZoneId);

    }

    private boolean checkIfGuestbookWritten(Member member, Pilgrimage pilgrimage) {
        // 방명록 작성 여부 확인

    }

    private boolean checkIfMultiGuestbookWritten(Member member, Pilgrimage pilgrimage) {
        // 방명록 다회 작성 여부 확인

    }
}
