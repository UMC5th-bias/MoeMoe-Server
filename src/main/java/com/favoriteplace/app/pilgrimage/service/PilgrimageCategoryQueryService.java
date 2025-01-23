package com.favoriteplace.app.pilgrimage.service;

import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.member.repository.MemberRepository;
import com.favoriteplace.app.pilgrimage.controller.dto.PilgrimageResponseDto;
import com.favoriteplace.app.pilgrimage.controller.dto.PilgrimageResponseDto.PilgrimageAddressDetailDto;
import com.favoriteplace.app.pilgrimage.controller.dto.PilgrimageResponseDto.PilgrimageCategoryRegionDto;
import com.favoriteplace.app.pilgrimage.converter.PilgrimageConverter;
import com.favoriteplace.app.pilgrimage.domain.Pilgrimage;
import com.favoriteplace.app.pilgrimage.repository.PilgrimageRepository;
import com.favoriteplace.app.pilgrimage.repository.VisitedPilgrimageRepository;
import com.favoriteplace.app.rally.controller.dto.RallyResponseDto.PilgrimageCategoryAnimeDto;
import com.favoriteplace.app.rally.converter.RallyConverter;
import com.favoriteplace.app.rally.domain.Address;
import com.favoriteplace.app.rally.domain.Rally;
import com.favoriteplace.app.rally.repository.AddressRepository;
import com.favoriteplace.app.rally.repository.RallyRepository;
import com.favoriteplace.global.auth.resolver.UserEmail;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PilgrimageCategoryQueryService {
    private final MemberRepository memberRepository;
    private final RallyRepository rallyRepository;
    private final VisitedPilgrimageRepository visitedPilgrimageRepository;
    private final AddressRepository addressRepository;
    private final PilgrimageRepository pilgrimageRepository;

    /***
     * 성지순례 애니 별 카테고리
     * @param email 사용자 이메일
     * @return
     */
    public List<PilgrimageCategoryAnimeDto> getCategoryAnime(@UserEmail String email) {
        List<Rally> rallyList = rallyRepository.findAllOrderByCreatedAt();

        if (email == null || email.isEmpty()) {
            return getPilgrimageAnimeCategoryWithoutMember(rallyList);
        }

        Member member = getMemberByEmail(email);
        return getPilgrimageAnimeCategoryWithMember(rallyList, member);
    }

    /***
     * 성지순례 지역 별 카테고리
     * @return state 별로 그룹화 한 지역 정보
     */
    public List<PilgrimageResponseDto.PilgrimageCategoryRegionDto> getCategoryRegion() {
        List<Address> address = addressRepository.findAll();

        Set<String> addressKeyList = getStateAll(address);
        Map<String, List<Address>> addressGroupByState = getDistrictGroupByState(address);

        return getPilgrimageCategoryRegionDtos(addressKeyList, addressGroupByState);
    }

    /***
     * 성지순례 지역 상세 카테고리
     * @param regionId
     * @return district 별 성지순례 리스트
     */
    public List<PilgrimageResponseDto.PilgrimageCategoryRegionDetailDto> getCategoryRegionDetail(Long regionId) {
        Address address = getAddressByRegion(regionId);
        List<Pilgrimage> pilgrimages = pilgrimageRepository.findByAddress(address);

        return pilgrimages.stream()
                .map(pilgrimage -> {
                    Rally rally = rallyRepository.findByPilgrimage(pilgrimage);
                    return PilgrimageConverter.toPilgrimageCategoryRegionDetailDto(rally.getName(), pilgrimage);
                })
                .toList();
    }

    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_FOUND));
    }

    private List<PilgrimageCategoryAnimeDto> getPilgrimageAnimeCategoryWithMember(List<Rally> rallyList,
                                                                                  Member member) {
        return rallyList.stream().map(rally -> {
            Long visitedPilgrimages = visitedPilgrimageRepository.findByDistinctCount(member.getId(), rally.getId());
            return RallyConverter.toPilgrimageCategoryAnimeDto(rally, visitedPilgrimages);
        }).toList();
    }

    private List<PilgrimageCategoryAnimeDto> getPilgrimageAnimeCategoryWithoutMember(List<Rally> rallyList) {
        return rallyList.stream()
                .map(rally -> RallyConverter.toPilgrimageCategoryAnimeDto(rally, 0L))
                .toList();
    }

    private Address getAddressByRegion(Long regionId) {
        return addressRepository.findById(regionId).orElseThrow(() ->
                new RestApiException(ErrorCode.ADDRESS_NOT_FOUND));
    }

    private Map<String, List<Address>> getDistrictGroupByState(List<Address> address) {
        return address.stream()
                .collect(Collectors.groupingBy(Address::getState));
    }

    private Set<String> getStateAll(List<Address> address) {
        return address.stream()
                .map(Address::getState)
                .collect(Collectors.toSet());
    }

    private List<PilgrimageCategoryRegionDto> getPilgrimageCategoryRegionDtos(Set<String> addressKeyList,
                                                                              Map<String, List<Address>> addressGroupByState) {
        return addressKeyList.stream().map(
                stateKey -> {
                    List<Address> addressList = addressGroupByState.get(stateKey);
                    List<PilgrimageAddressDetailDto> addressDetailDtos = getPilgrimageAddressDetailDtos(
                            addressList);
                    return PilgrimageConverter.toPilgrimageCategoryRegionDto(stateKey, addressDetailDtos);
                }
        ).toList();
    }

    private List<PilgrimageAddressDetailDto> getPilgrimageAddressDetailDtos(List<Address> addressList) {
        return addressList.stream()
                .map(PilgrimageConverter::toPilgrimageAddressDetailDto)
                .toList();
    }
}
