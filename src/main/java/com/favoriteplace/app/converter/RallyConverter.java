package com.favoriteplace.app.converter;

import com.favoriteplace.app.domain.travel.Address;
import com.favoriteplace.app.domain.travel.Pilgrimage;
import com.favoriteplace.app.domain.travel.Rally;
import com.favoriteplace.app.dto.travel.RallyDto;

import java.util.List;

public class RallyConverter {
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
                    .itemImage(rally.getItem().getImage().getUrl())
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
                    .itemImage(rally.getItem().getImage().getUrl())
                    .isLike(false)
                    .build();
        }
    }

    public static RallyDto.RallyAddressPilgrimageDto toRallyAddressPilgrimageDto(Pilgrimage pilgrimage){
        return RallyDto.RallyAddressPilgrimageDto.builder()
                .id(pilgrimage.getId())
                .pilgrimageAddress(pilgrimage.getDetailAddress())
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

}
