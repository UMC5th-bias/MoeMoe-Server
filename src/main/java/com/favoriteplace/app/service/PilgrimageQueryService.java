package com.favoriteplace.app.service;

import com.favoriteplace.app.converter.RallyConverter;
import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.travel.*;
import com.favoriteplace.app.dto.travel.PilgrimageDto;
import com.favoriteplace.app.dto.travel.RallyDto;
import com.favoriteplace.app.repository.*;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.ErrorResponse;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PilgrimageQueryService {
    private final PilgrimageRepository pilgrimageRepository;
    private final RallyRepository rallyRepository;
    private final LikedRallyRepository likedRallyRepository;
    private final VisitedPilgrimageRepository visitedPilgrimageRepository;
    private final AddressRepository addressRepository;

    // 성지순례 랠리 상세
    public RallyDto.RallyDetailResponseDto getRallyDetail(Long rallyId, Member member) {
        Rally rally = rallyRepository.findById(rallyId).orElseThrow(
                ()-> new RestApiException(ErrorCode.RALLY_NOT_FOUND));
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

    // 성지순례 랠리 장소 리스트
    // 사용자 정보 없을 때 RallyAddressPilgrimageDto.isVisited->false
    public RallyDto.RallyAddressListDto getRallyAddressList(Long rallyId, Member member) {
        Rally rally = rallyRepository.findById(rallyId).orElseThrow(
                ()-> new RestApiException(ErrorCode.RALLY_NOT_FOUND));

        // 주소, 성지순례 리스트 정보 담은 주소 리스트 생성
        // 랠리에서 주소 나누는 단계에서 문제 발생함. 조회 제대로 안 됨.
        List<Address> addressList = addressRepository.findByPilgrimages_Rally(rally);
        if (addressList.isEmpty()) throw new RestApiException(ErrorCode.PILGRIMAGE_NOT_FOUND);
        List<RallyDto.RallyAddressDto> addressDtoList = addressList.stream()
                .map(address -> rallyAddressDtoList(rally, address))
                .collect(Collectors.toList());
        // 회원이라면 방문기록 수정하기
        RallyDto.RallyAddressListDto result;
        if (member != null){
            addressDtoList.stream().forEach(rallyAddressDto -> rallyAddressDto.getPilgrimage()
                    .stream()
                    .forEach(rallyAddressPilgrimageDto -> checkVisitedPilgrimage(rallyAddressPilgrimageDto, member)));
            result = RallyConverter.toRallyAddressListDto(rally, addressDtoList, 0L);
        } else {
            // RallyAddressListDto 생성 후 반환하기
            Long myPilgrimageNumber = Long.valueOf(visitedPilgrimageRepository.findByMemberAndPilgrimage_Rally(member, rally).size());
            result = RallyConverter.toRallyAddressListDto(rally, addressDtoList, myPilgrimageNumber);
        }
        return result;
    }

    private void checkVisitedPilgrimage(RallyDto.RallyAddressPilgrimageDto dto, Member member){
        Pilgrimage pilgrimage = pilgrimageRepository.findById(dto.getId())
                .orElseThrow(() -> new RestApiException(ErrorCode.PILGRIMAGE_NOT_FOUND));
        List<VisitedPilgrimage> count = visitedPilgrimageRepository.findByPilgrimageAndMember(pilgrimage, member);
        if (!count.isEmpty()){
            dto.setIsVisited(true);
        }
    }

    // 어떤 랠리의 어떤 주소에 대한 모든 성지순례 조회 (isVisited false로 초기화)
    private RallyDto.RallyAddressDto rallyAddressDtoList(Rally rally, Address address){
        List<Pilgrimage> pilgrimageList = pilgrimageRepository.findByRallyAndAddress(rally, address);

        List<RallyDto.RallyAddressPilgrimageDto> dtoList = pilgrimageList.stream().map(pilgrimage ->
                        RallyConverter.toRallyAddressPilgrimageDto(pilgrimage))
                .collect(Collectors.toList());

        return RallyConverter.toRallyAddressDto(address, dtoList);
    }

    // 성지순례 랠리 장소 상세
    public PilgrimageDto.PilgrimageDetailDto getPilgrimageDetail(Long pilgrimageId, Member member) {
        Pilgrimage pilgrimage = pilgrimageRepository.findById(pilgrimageId)
                .orElseThrow(() -> new RestApiException(ErrorCode.PILGRIMAGE_NOT_FOUND));


        return null;
    }
}
