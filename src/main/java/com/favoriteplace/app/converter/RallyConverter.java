package com.favoriteplace.app.converter;

import com.favoriteplace.app.domain.travel.Address;
import com.favoriteplace.app.pilgrimage.domain.Pilgrimage;
import com.favoriteplace.app.domain.travel.Rally;
import com.favoriteplace.app.dto.travel.RallyResponseDto;

import java.util.List;

public class RallyConverter {
    public static RallyResponseDto.RallyTrendingDto toRallyTrendingDto(Rally rally, Long myPilgrimageNumber){
        return RallyResponseDto.RallyTrendingDto.builder()
                .id(rally.getId())
                .name(rally.getName())
                .pilgrimageNumber(rally.getPilgrimageNumber())
                .myPilgrimageNumber(myPilgrimageNumber)
                .image(rally.getImage().getUrl())
                .build();
    }
    public static RallyResponseDto.RallyDetailResponseDto toRallyDetailResponseDto(Rally rally, Long myPilgrimageNumber, Boolean isLike, Boolean isMember){
        // 회원
        if (isMember) {
            return RallyResponseDto.RallyDetailResponseDto.builder()
                    .name(rally.getName())
                    .pilgrimageNumber(rally.getPilgrimageNumber())
                    .myPilgrimageNumber(myPilgrimageNumber)
                    .image(rally.getImage().getUrl())
                    .description(rally.getDescription())
                    .achieveNumber(rally.getAchieveNumber())
                    .itemImage(rally.getItem().getDefaultImage().getUrl())
                    .isLike(isLike)
                    .build();
        } // 비회원
        else {
            return RallyResponseDto.RallyDetailResponseDto.builder()
                    .name(rally.getName())
                    .pilgrimageNumber(rally.getPilgrimageNumber())
                    .myPilgrimageNumber(0L)
                    .image(rally.getImage().getUrl())
                    .description(rally.getDescription())
                    .achieveNumber(rally.getAchieveNumber())
                    .itemImage(rally.getItem().getDefaultImage().getUrl())
                    .isLike(false)
                    .build();
        }
    }

    public static RallyResponseDto.RallyAddressPilgrimageDto toRallyAddressPilgrimageDto(Pilgrimage pilgrimage){
        return RallyResponseDto.RallyAddressPilgrimageDto.builder()
                .id(pilgrimage.getId())
                .detailAddress(pilgrimage.getDetailAddress())
                .image(pilgrimage.getVirtualImage().getUrl())
                .isVisited(false)
                .build();
    }

    public static RallyResponseDto.RallyAddressDto toRallyAddressDto(Address address, List<RallyResponseDto.RallyAddressPilgrimageDto> pilgrimageList){
        return RallyResponseDto.RallyAddressDto.builder()
                .address(address.getState()+" "+address.getDistrict())
                .pilgrimage(pilgrimageList)
                .build();
    }

    public static RallyResponseDto.RallyAddressListDto toRallyAddressListDto(Rally rally, List<RallyResponseDto.RallyAddressDto> dtos, Long myPilgrimageNumber){
        return RallyResponseDto.RallyAddressListDto.builder()
                .name(rally.getName())
                .pilgrimageNumber(rally.getPilgrimageNumber())
                .myPilgrimageNumber(myPilgrimageNumber)
                .image(rally.getImage().getUrl())
                .rally(dtos)
                .build();
    }

    public static RallyResponseDto.PilgrimageCategoryAnimeDto toPilgrimageCategoryAnimeDto(Rally rally, Long myPilgrimageNumber){
        return RallyResponseDto.PilgrimageCategoryAnimeDto.builder()
                .id(rally.getId())
                .name(rally.getName())
                .pilgrimageNumber(rally.getPilgrimageNumber())
                .myPilgrimageNumber(myPilgrimageNumber)
                .image(rally.getImage().getUrl())
                .build();
    }

    public static RallyResponseDto.SearchAnimeDto toSearchAnimeDto(Rally rally, Long visitedPilgrimages) {
        return RallyResponseDto.SearchAnimeDto.builder()
                .name(rally.getName())
                .pilgrimageNumber(rally.getPilgrimageNumber())
                .myPilgrimageNumber(visitedPilgrimages)
                .image(rally.getImage().getUrl())
                .build();
    }

    public static RallyResponseDto.SearchRegionDto toSearchRegionDto(String name, List<RallyResponseDto.SearchRegionDetailDto> pilgrimages) {
        return RallyResponseDto.SearchRegionDto.builder()
                .address(name)
                .rallies(pilgrimages)
                .build();
    }

    public static RallyResponseDto.SearchRegionDetailDto toSearchRegionDetailDto(Pilgrimage pilgrimage){
        return RallyResponseDto.SearchRegionDetailDto.builder()
                .name(pilgrimage.getRallyName())
                .image(pilgrimage.getVirtualImage().getUrl())
                .detailAddress(pilgrimage.getDetailAddress())
                .build();
    }
}
