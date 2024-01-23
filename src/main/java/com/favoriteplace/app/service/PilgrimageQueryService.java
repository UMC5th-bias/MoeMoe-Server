package com.favoriteplace.app.service;

import com.favoriteplace.app.converter.PilgrimageConverter;
import com.favoriteplace.app.converter.RallyConverter;
import com.favoriteplace.app.domain.Image;
import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.community.GuestBook;
import com.favoriteplace.app.domain.community.HashTag;
import com.favoriteplace.app.domain.travel.*;
import com.favoriteplace.app.dto.travel.PilgrimageDto;
import com.favoriteplace.app.dto.travel.RallyDto;
import com.favoriteplace.app.repository.*;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PilgrimageQueryService {
    private final PilgrimageRepository pilgrimageRepository;
    private final RallyRepository rallyRepository;
    private final LikedRallyRepository likedRallyRepository;
    private final VisitedPilgrimageRepository visitedPilgrimageRepository;
    private final AddressRepository addressRepository;
    private final GuestBookRepository guestBookRepository;
    private final HashtagRepository hashtagRepository;
    private final ImageRepository imageRepository;

    /***
     * 랠리 상세
     * @param rallyId
     * @param member
     * @return 랠리 상세페이지 dto
     */
    public RallyDto.RallyDetailResponseDto getRallyDetail(Long rallyId, Member member) {
        Rally rally = rallyRepository.findById(rallyId).orElseThrow(
                ()-> new RestApiException(ErrorCode.RALLY_NOT_FOUND));
        if (member == null){
            return RallyConverter.toRallyDetailResponseDto(rally, 0L, false, false);
        }
        LikedRally isLikeList = likedRallyRepository.findByRallyAndMember(rally, member);
        List<VisitedPilgrimage> pilgrimageNumber = visitedPilgrimageRepository
                .findByMemberAndPilgrimage_Rally(member, rally);
        if (isLikeList == null) {
            return RallyConverter.toRallyDetailResponseDto(rally, Long.valueOf(pilgrimageNumber.size()), false, true);
        }
        return RallyConverter.toRallyDetailResponseDto(rally, Long.valueOf(pilgrimageNumber.size()), true, true);
    }

    /***
     * 한 랠리의 성지순례 리스트
     * @param rallyId
     * @param member
     * @return 한 랠리에 대한 성지순례 리스트 dto
     */
    public RallyDto.RallyAddressListDto getRallyAddressList(Long rallyId, Member member) {
        Rally rally = rallyRepository.findById(rallyId).orElseThrow(
                ()-> new RestApiException(ErrorCode.RALLY_NOT_FOUND));

        // 주소, 성지순례 리스트 정보 담은 주소 리스트 생성
        List<Address> addressList = addressRepository.findByPilgrimages_Rally(rally);
        if (addressList.isEmpty()) throw new RestApiException(ErrorCode.PILGRIMAGE_NOT_FOUND);

        List<RallyDto.RallyAddressDto> addressDtoList = addressList.stream()
                .map(address -> rallyAddressDtoList(rally, address))
                .collect(Collectors.toList());

        // 회원이라면 방문기록 수정하기
        if (member != null){
            addressDtoList.stream().forEach(rallyAddressDto ->
                    rallyAddressDto.getPilgrimage()
                    .stream()
                    .forEach(rallyAddressPilgrimageDto ->
                            checkVisitedPilgrimage(rallyAddressPilgrimageDto, member)));
            return RallyConverter.toRallyAddressListDto(rally, addressDtoList, 0L);
        }
        // RallyAddressListDto 생성 후 반환하기
        Long myPilgrimageNumber = Long.valueOf(visitedPilgrimageRepository
                .findDistinctByMemberAndPilgrimage_Rally(member, rally).size());
        return RallyConverter.toRallyAddressListDto(rally, addressDtoList, myPilgrimageNumber);
    }

    private void checkVisitedPilgrimage(RallyDto.RallyAddressPilgrimageDto dto, Member member){
        Pilgrimage pilgrimage = pilgrimageRepository.findById(dto.getId())
                .orElseThrow(() -> new RestApiException(ErrorCode.PILGRIMAGE_NOT_FOUND));
        List<VisitedPilgrimage> count = visitedPilgrimageRepository
                .findByPilgrimageAndMemberOrderByCreatedAtDesc(pilgrimage, member);
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

    /***
     * 성지순례 랠리 장소 상세
     * @param pilgrimageId
     * @param member
     * @return 성지순례 상세페이지 dto
     */
    public PilgrimageDto.PilgrimageDetailDto getPilgrimageDetail(Long pilgrimageId, Member member) {
        Pilgrimage pilgrimage = pilgrimageRepository.findById(pilgrimageId)
                .orElseThrow(() -> new RestApiException(ErrorCode.PILGRIMAGE_NOT_FOUND));
        List<VisitedPilgrimage> visitedPilgrimages = visitedPilgrimageRepository
                .findDistinctByMemberAndPilgrimage_Rally(member, pilgrimage.getRally());
        PilgrimageDto.PilgrimageDetailDto result = PilgrimageConverter.toPilgrimageDetailDto(pilgrimage, Long.valueOf(visitedPilgrimages.size()));

        // 이 성지순례에 인증 기록이 있다면 isWritable -> true
        List<VisitedPilgrimage> visitedLog = visitedPilgrimageRepository
                .findByPilgrimageAndMemberOrderByCreatedAtDesc(pilgrimage, member);
        if (visitedLog.size() >= 1) {
            result.setIsWritable(true);
        }
        // 이 성지순례에 인증 기록이 두 개 이상이라면 isisMultiWritable -> true
        if (visitedLog.size() >= 2) {
            result.setIsMultiWritable(true);
        }
        return result;
    }

    //

    /***
     * 성지순례 메인 (내 성지순례 + 인증글)
     * @param member
     * @return 내 성지순례, 내 인증글 dto
     */
    public PilgrimageDto.MyPilgrimageDto getMyPilgrimageDto(Member member) {
        if (member == null) {
            return PilgrimageConverter.toMyPilgrimageDto();
        }
        // 관심있는 랠리
        List<PilgrimageDto.LikedRallyDto> likedRallyDtos = getLikedRally(member);
        // 내 성지순례 인증글 (시간순)
        List<PilgrimageDto.MyGuestBookDto> myGuestBookDtos = getMyGuestBook(member);

        return PilgrimageConverter.toMyPilgrimageDto(likedRallyDtos, myGuestBookDtos);
    }

    private List<PilgrimageDto.LikedRallyDto> getLikedRally(Member member) {
        List<LikedRally> likedRally = likedRallyRepository.findByMember(member);
        return likedRally.stream().map(
                        likeRally -> {
                            Rally rally = rallyRepository.findById(likeRally.getRally().getId())
                                    .orElseThrow(()->new RestApiException(ErrorCode.RALLY_NOT_FOUND));
                            return PilgrimageConverter.toLikedRallyDto(rally);
                        })
                .collect(Collectors.toList());
    }

    private List<PilgrimageDto.MyGuestBookDto> getMyGuestBook(Member member){
        List<GuestBook> guestBooks = guestBookRepository.findByMemberOrderByCreatedAtDesc(member);
        return guestBooks.stream().map(
                guestBook -> {
                    Image image = imageRepository.findFirstByGuestBook(guestBook);
                    List<HashTag> hashTags = hashtagRepository.findAllByGuestBookId(guestBook.getId());
                    List<String> hashTagsDto = hashTags.stream().map(hashTag -> hashTag.getTagName()).collect(Collectors.toList());
                    return PilgrimageConverter.toMyGuestBookDto(guestBook, image, hashTagsDto);
                }
        ).collect(Collectors.toList());
    }

    /***
     * 이달의 추천 랠리
     * @return 당월 1일 부터 현재까지의 좋아요 집계 1위 랠리
     */
    public RallyDto.RallyTrendingDto getRallyTrending() {
        return null;
    }

    /***
     * 성지순례 애니 별 카테고리
     * @param member
     * @return
     */
    public List<RallyDto.PilgrimageCategoryAnimeDto> getCategoryAnime(Member member) {
        // 전체 랠리 최신 순으로 조회하기
        List<Rally> rallyList = rallyRepository.findAllOrderByCreatedAt();
        if (member == null) {
            return rallyList.stream()
                    .map(rally -> RallyConverter.toPilgrimageCategoryAnimeDto(rally, 0L))
                    .collect(Collectors.toList());
        }
        return rallyList.stream().map(rally->{
            List<VisitedPilgrimage> visitedPilgrimages = visitedPilgrimageRepository.findDistinctByMemberAndPilgrimage_Rally(member, rally);
            return RallyConverter.toPilgrimageCategoryAnimeDto(rally, Long.valueOf(visitedPilgrimages.size()));
        }).collect(Collectors.toList());
    }

    /***
     * 성지순례 지역 별 카테고리
     * @return state 별로 그룹화 한 지역 정보
     */
    public List<PilgrimageDto.PilgrimageCategoryRegionDto> getCategoryRegion() {
        List<Address> address = addressRepository.findAll();

        // 전체 state 추출
        Set<String> addressKeyList = address.stream()
                .map(Address::getState)
                .collect(Collectors.toSet());

        // 전체 address 정보 state 별로 그룹화
        Map<String, List<Address>> addressGroupByState = address.stream()
                .collect(Collectors.groupingBy(Address::getState));

        List<PilgrimageDto.PilgrimageCategoryRegionDto> dtos = addressKeyList.stream().map(
                stateKey -> {
                    List<Address> addressList = addressGroupByState.get(stateKey);
                    List<PilgrimageDto.PilgrimageAddressDetailDto> addressDetailDtos = addressList.stream().map(addressDetail ->
                            PilgrimageConverter.toPilgrimageAddressDetailDto(addressDetail)
                    ).collect(Collectors.toList());
                    return PilgrimageConverter.toPilgrimageCategoryRegionDto(stateKey, addressDetailDtos);
                }
        ).collect(Collectors.toList());
        return dtos;
    }

    /***
     * 성지순례 지역 상세 카테고리
     * @param regionId
     * @return district 별 성지순례 리스트
     */
    public PilgrimageDto.PilgrimageCategoryRegionDetailDto getCategoryRegionDetail(Long regionId) {
        return null;
    }
}
