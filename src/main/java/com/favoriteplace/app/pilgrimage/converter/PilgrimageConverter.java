package com.favoriteplace.app.pilgrimage.converter;

import com.favoriteplace.app.common.domain.Image;
import com.favoriteplace.app.community.domain.GuestBook;
import com.favoriteplace.app.rally.domain.Address;
import com.favoriteplace.app.pilgrimage.domain.Pilgrimage;
import com.favoriteplace.app.rally.domain.Rally;
import com.favoriteplace.app.pilgrimage.controller.dto.PilgrimageResponseDto;

import java.util.ArrayList;
import java.util.List;

public class PilgrimageConverter {
    public static PilgrimageResponseDto.PilgrimageDetailDto toPilgrimageDetailDto(Pilgrimage pilgrimage, Long myPilgrimageNumber){
        return PilgrimageResponseDto.PilgrimageDetailDto.builder()
                .rallyName(pilgrimage.getRally().getName())
                .pilgrimageNumber(pilgrimage.getRally().getPilgrimageNumber())
                .myPilgrimageNumber(myPilgrimageNumber)
                .image(pilgrimage.getVirtualImage().getUrl())
                .realImage(pilgrimage.getRealImage().getUrl())
                .address(pilgrimage.getDetailAddress())
                .addressEn(pilgrimage.getDetailAddressEn())
                .addressJp(pilgrimage.getDetailAddressJp())
                .latitude(pilgrimage.getLatitude())
                .longitude(pilgrimage.getLongitude())
                .isCertified(true)
                .isWritable(false)
                .isMultiWritable(false)
                .build();
    }

    // 내 성지순례 (비회원)
    public static PilgrimageResponseDto.MyPilgrimageDto toMyPilgrimageDto() {
        return PilgrimageResponseDto.MyPilgrimageDto.builder()
                .likedRallySize(0L)
                .likedRally(new ArrayList<>())
                .guestBookSize(0L)
                .guestBook(new ArrayList<>())
                .build();
    }

    // 내 성지순례 (회원)
    public static PilgrimageResponseDto.MyPilgrimageDto toMyPilgrimageDto(
            List<PilgrimageResponseDto.LikedRallyDto> likedRallyDtos, List<PilgrimageResponseDto.MyGuestBookDto> myGuestBookDtos) {
        return PilgrimageResponseDto.MyPilgrimageDto.builder()
                .likedRallySize(Long.valueOf(likedRallyDtos.size()))
                .likedRally(likedRallyDtos)
                .guestBookSize(Long.valueOf(myGuestBookDtos.size()))
                .guestBook(myGuestBookDtos)
                .build();
    }

    public static PilgrimageResponseDto.LikedRallyDto toLikedRallyDto(Rally rally){
        return PilgrimageResponseDto.LikedRallyDto.builder()
                .id(rally.getId())
                .name(rally.getName())
                .image(rally.getImage().getUrl())
                .build();
    }

    // 날짜 규격대로 변경 필요함
    public static PilgrimageResponseDto.MyGuestBookDto toMyGuestBookDto(GuestBook guestBook, Image mainImg, List<String> hashTags){
        return PilgrimageResponseDto.MyGuestBookDto.builder()
                .id(guestBook.getId())
                .title(guestBook.getTitle())
                .createdAt(guestBook.getCreatedAt().toString())
                .image(mainImg != null ? mainImg.getUrl() : null)
                .hashTag(hashTags)
                .build();
    }

    public static PilgrimageResponseDto.PilgrimageCategoryRegionDto toPilgrimageCategoryRegionDto(String state, List<PilgrimageResponseDto.PilgrimageAddressDetailDto> dtos){
        return PilgrimageResponseDto.PilgrimageCategoryRegionDto.builder()
                .state(state)
                .detail(dtos)
                .build();
    }

    public static PilgrimageResponseDto.PilgrimageAddressDetailDto toPilgrimageAddressDetailDto(Address address){
        return PilgrimageResponseDto.PilgrimageAddressDetailDto.builder()
                .id(address.getId())
                .district(address.getDistrict())
                .build();
    }

    public static PilgrimageResponseDto.PilgrimageCategoryRegionDetailDto toPilgrimageCategoryRegionDetailDto(String title, Pilgrimage pilgrimage){
        return PilgrimageResponseDto.PilgrimageCategoryRegionDetailDto.builder()
                .id(pilgrimage.getId())
                .title(title)
                .detailAddress(pilgrimage.getDetailAddress())
                .image(pilgrimage.getVirtualImage().getUrl())
                .latitude(pilgrimage.getLatitude())
                .longitude(pilgrimage.getLongitude())
                .build();
    }

}
