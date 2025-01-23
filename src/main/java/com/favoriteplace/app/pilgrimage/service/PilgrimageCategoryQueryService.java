package com.favoriteplace.app.pilgrimage.service;

import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.pilgrimage.controller.dto.PilgrimageResponseDto;
import com.favoriteplace.app.pilgrimage.controller.dto.PilgrimageResponseDto.PilgrimageCategoryRegionDto;
import com.favoriteplace.app.pilgrimage.converter.PilgrimageConverter;
import com.favoriteplace.app.pilgrimage.domain.Pilgrimage;
import com.favoriteplace.app.pilgrimage.repository.PilgrimageRepository;
import com.favoriteplace.app.pilgrimage.repository.VisitedPilgrimageRepository;
import com.favoriteplace.app.rally.controller.dto.RallyResponseDto;
import com.favoriteplace.app.rally.controller.dto.RallyResponseDto.PilgrimageCategoryAnimeDto;
import com.favoriteplace.app.rally.converter.RallyConverter;
import com.favoriteplace.app.rally.domain.Address;
import com.favoriteplace.app.rally.domain.Rally;
import com.favoriteplace.app.rally.repository.AddressRepository;
import com.favoriteplace.app.rally.repository.RallyRepository;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PilgrimageCategoryQueryService {
    private final RallyRepository rallyRepository;
    private final VisitedPilgrimageRepository visitedPilgrimageRepository;
    private final AddressRepository addressRepository;
    private final PilgrimageRepository pilgrimageRepository;

    /***
     * 성지순례 애니 별 카테고리
     * @param member
     * @return
     */
    public List<PilgrimageCategoryAnimeDto> getCategoryAnime(Member member) {
        // 전체 랠리 최신 순으로 조회하기
        List<Rally> rallyList = rallyRepository.findAllOrderByCreatedAt();
        if (member == null) {
            return rallyList.stream()
                    .map(rally -> RallyConverter.toPilgrimageCategoryAnimeDto(rally, 0L))
                    .collect(Collectors.toList());
        }
        return rallyList.stream().map(rally -> {
            Long visitedPilgrimages = visitedPilgrimageRepository.findByDistinctCount(member.getId(), rally.getId());
            return RallyConverter.toPilgrimageCategoryAnimeDto(rally, visitedPilgrimages);
        }).collect(Collectors.toList());
    }

    /***
     * 성지순례 지역 별 카테고리
     * @return state 별로 그룹화 한 지역 정보
     */
    public List<PilgrimageResponseDto.PilgrimageCategoryRegionDto> getCategoryRegion() {
        List<Address> address = addressRepository.findAll();

        // 전체 state 추출
        Set<String> addressKeyList = address.stream()
                .map(Address::getState)
                .collect(Collectors.toSet());

        // 전체 address 정보 state 별로 그룹화
        Map<String, List<Address>> addressGroupByState = address.stream()
                .collect(Collectors.groupingBy(Address::getState));

        return getPilgrimageCategoryRegionDtos(addressKeyList, addressGroupByState);
    }

    /***
     * 성지순례 지역 상세 카테고리
     * @param regionId
     * @return district 별 성지순례 리스트
     */
    public List<PilgrimageResponseDto.PilgrimageCategoryRegionDetailDto> getCategoryRegionDetail(Long regionId) {
        Address address = addressRepository.findById(regionId).orElseThrow(() ->
                new RestApiException(ErrorCode.ADDRESS_NOT_FOUND));
        List<Pilgrimage> pilgrimages = pilgrimageRepository.findByAddress(address);

        return pilgrimages.stream()
                .map(pilgrimage -> {
                    Rally rally = rallyRepository.findByPilgrimage(pilgrimage);
                    return PilgrimageConverter.toPilgrimageCategoryRegionDetailDto(rally.getName(), pilgrimage);
                })
                .collect(Collectors.toList());
    }

    @NotNull
    private static List<PilgrimageCategoryRegionDto> getPilgrimageCategoryRegionDtos(Set<String> addressKeyList,
                                                                                     Map<String, List<Address>> addressGroupByState) {
        List<PilgrimageCategoryRegionDto> dtos = addressKeyList.stream().map(
                stateKey -> {
                    List<Address> addressList = addressGroupByState.get(stateKey);
                    List<PilgrimageResponseDto.PilgrimageAddressDetailDto> addressDetailDtos = addressList.stream()
                            .map(addressDetail ->
                                    PilgrimageConverter.toPilgrimageAddressDetailDto(addressDetail)
                            ).collect(Collectors.toList());
                    return PilgrimageConverter.toPilgrimageCategoryRegionDto(stateKey, addressDetailDtos);
                }
        ).collect(Collectors.toList());
        return dtos;
    }
}
