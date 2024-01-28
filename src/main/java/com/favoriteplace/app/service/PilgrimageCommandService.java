package com.favoriteplace.app.service;

import com.favoriteplace.app.converter.CommonConverter;
import com.favoriteplace.app.converter.PointHistoryConverter;
import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.enums.PointType;
import com.favoriteplace.app.domain.item.PointHistory;
import com.favoriteplace.app.domain.travel.LikedRally;
import com.favoriteplace.app.domain.travel.Pilgrimage;
import com.favoriteplace.app.domain.travel.Rally;
import com.favoriteplace.app.domain.travel.VisitedPilgrimage;
import com.favoriteplace.app.dto.CommonResponseDto;
import com.favoriteplace.app.dto.travel.PilgrimageDto;
import com.favoriteplace.app.repository.*;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PilgrimageCommandService {
    private final RallyRepository rallyRepository;
    private final PilgrimageRepository pilgrimageRepository;
    private final LikedRallyRepository likedRallyRepository;
    private final VisitedPilgrimageRepository visitedPilgrimageRepository;
    private final PointHistoryRepository pointHistoryRepository;

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
            LikedRally newLikedRally = LikedRally.builder()
                    .rally(rally)
                    .member(member)
                    .build();
            likedRallyRepository.save(newLikedRally);
            return CommonConverter.toPostResponseDto(true, "찜 목록에 추가됐습니다.");
        } else {
            likedRallyRepository.delete(likedRally);
            return CommonConverter.toPostResponseDto(true, "찜을 취소했습니다.");
        }
    }

    /***
     * 성지순례 방문 인증하기
     * @param pilgrimageId 성지순례 아이디
     * @param member 인증한 사용자
     * @return
     */
    public CommonResponseDto.PostResponseDto certifyToPilgrimage(Long pilgrimageId,
                                                                 Member member,
                                                                 PilgrimageDto.PilgrimageCertifyRequestDto form) {
        Pilgrimage pilgrimage = pilgrimageRepository.findById(pilgrimageId).orElseThrow(
                ()->new RestApiException(ErrorCode.PILGRIMAGE_NOT_FOUND));

        // 24시간 이내 방문이력 확인
        List<VisitedPilgrimage> visitedPilgrimages = visitedPilgrimageRepository
                .findByPilgrimageAndMemberOrderByCreatedAtDesc(pilgrimage, member);

        if (visitedPilgrimages.isEmpty()
                || (!visitedPilgrimages.isEmpty() && visitedPilgrimages.get(0).getPilgrimage().getCreatedAt().plusHours(24L).isBefore(LocalDateTime.now()))) {
            // 현재 좌표가 성지순례 장소 좌표 기준 +-0.00135 이내인지 확인
            if (pilgrimage.getLatitude() + 0.00135 < form.getLatitude() || pilgrimage.getLatitude() - 0.00135 > form.getLatitude()
                    || pilgrimage.getLongitude() + 0.00135 < form.getLongitude() || pilgrimage.getLongitude() - 0.00135 > form.getLongitude())
                throw new RestApiException(ErrorCode.PILGRIMAGE_CAN_NOT_CERTIFIED);

            // 성공 시 포인트 지급 -> 15p & visitedPilgrimage 추가
            VisitedPilgrimage newVisited = VisitedPilgrimage.builder().pilgrimage(pilgrimage).member(member).build();
            visitedPilgrimageRepository.save(newVisited);
            pointHistoryRepository.save(PointHistoryConverter.toPointHistory(member, 15L, PointType.ACQUIRE));

            // 전체 랠리를 성공했는지 조회 후 맞다면... ->


            return CommonConverter.toPostResponseDto(true, "인증에 성공했습니다.");
        } else throw new RestApiException(ErrorCode.PILGRIMAGE_ALREADY_CERTIFIED);
    }

    /***
     * 성지순례 방문 인증글 작성하기
     * @param pilgrimageId 성지순례 아이디
     * @param member 인증한 사용자
     * @return
     */
    public CommonResponseDto.PostResponseDto postToPilgrimage(Long pilgrimageId, Member member) {
        return null;
    }
}
