package com.favoriteplace.app.converter;

import com.favoriteplace.app.domain.travel.Address;
import com.favoriteplace.app.domain.travel.Pilgrimage;
import com.favoriteplace.app.domain.travel.Rally;
import com.favoriteplace.app.dto.travel.RallyDto;

import java.util.List;

public class RallyConverter {
    public static RallyDto.RallyTrendingDto toRallyTrendingDto(Rally rally, Long myPilgrimageNumber){
        return RallyDto.RallyTrendingDto.builder()
                .id(rally.getId())
                .name(rally.getName())
                .pilgrimageNumber(rally.getPilgrimageNumber())
                .myPilgrimageNumber(myPilgrimageNumber)
                .image(rally.getImage().getUrl())
                .build();
    }
    public static RallyDto.RallyDetailResponseDto toRallyDetailResponseDto(Rally rally, Long myPilgrimageNumber, Boolean isLike, Boolean isMember){
        // 회원
        if (isMember) {
            return RallyDto.RallyDetailResponseDto.builder()
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
            return RallyDto.RallyDetailResponseDto.builder()
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

    public static RallyDto.RallyAddressPilgrimageDto toRallyAddressPilgrimageDto(Pilgrimage pilgrimage){
        return RallyDto.RallyAddressPilgrimageDto.builder()
                .id(pilgrimage.getId())
                .detailAddress(pilgrimage.getDetailAddress())
                .image(pilgrimage.getVirtualImage().getUrl())
                .isVisited(false)
                .build();
    }

    public static RallyDto.RallyAddressDto toRallyAddressDto(Address address, List<RallyDto.RallyAddressPilgrimageDto> pilgrimageList){
        return RallyDto.RallyAddressDto.builder()
                .address(address.getState()+" "+address.getDistrict())
                .pilgrimage(pilgrimageList)
                .build();
    }

    public static RallyDto.RallyAddressListDto toRallyAddressListDto(Rally rally, List<RallyDto.RallyAddressDto> dtos, Long myPilgrimageNumber){
        return RallyDto.RallyAddressListDto.builder()
                .name(rally.getName())
                .pilgrimageNumber(rally.getPilgrimageNumber())
                .myPilgrimageNumber(myPilgrimageNumber)
                .image(rally.getImage().getUrl())
                .rally(dtos)
                .build();
    }

    public static RallyDto.PilgrimageCategoryAnimeDto toPilgrimageCategoryAnimeDto(Rally rally, Long myPilgrimageNumber){
        return RallyDto.PilgrimageCategoryAnimeDto.builder()
                .id(rally.getId())
                .name(rally.getName())
                .pilgrimageNumber(rally.getPilgrimageNumber())
                .myPilgrimageNumber(myPilgrimageNumber)
                .image(rally.getImage().getUrl())
                .build();
    }

    public static RallyDto.SearchAnimeDto toSearchAnimeDto(Rally rally, Long visitedPilgrimages) {
        return RallyDto.SearchAnimeDto.builder()
                .name(rally.getName())
                .pilgrimageNumber(rally.getPilgrimageNumber())
                .myPilgrimageNumber(visitedPilgrimages)
                .image(rally.getImage().getUrl())
                .build();
    }

    public static RallyDto.SearchRegionDto toSearchRegionDto(String name, List<RallyDto.SearchRegionDetailDto> pilgrimages) {
        return RallyDto.SearchRegionDto.builder()
                .address(name)
                .rallies(pilgrimages)
                .build();
    }

    public static RallyDto.SearchRegionDetailDto toSearchRegionDetailDto(Pilgrimage pilgrimage){
        return RallyDto.SearchRegionDetailDto.builder()
                .name(pilgrimage.getRallyName())
                .image(pilgrimage.getVirtualImage().getUrl())
                .detailAddress(pilgrimage.getDetailAddress())
                .build();
    }
}
