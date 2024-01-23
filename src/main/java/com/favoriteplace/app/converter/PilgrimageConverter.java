package com.favoriteplace.app.converter;

import com.favoriteplace.app.domain.Image;
import com.favoriteplace.app.domain.community.GuestBook;
import com.favoriteplace.app.domain.community.HashTag;
import com.favoriteplace.app.domain.travel.Address;
import com.favoriteplace.app.domain.travel.Pilgrimage;
import com.favoriteplace.app.domain.travel.Rally;
import com.favoriteplace.app.dto.travel.PilgrimageDto;

import java.util.ArrayList;
import java.util.List;

public class PilgrimageConverter {
    public static PilgrimageDto.PilgrimageDetailDto toPilgrimageDetailDto(Pilgrimage pilgrimage, Long myPilgrimageNumber){
        return PilgrimageDto.PilgrimageDetailDto.builder()
                .rallyName(pilgrimage.getRally().getName())
                .pilgrimageNumber(pilgrimage.getRally().getPilgrimageNumber())
                .myPilgrimageNumber(myPilgrimageNumber)
                .image(pilgrimage.getVirtualImage().getUrl())
                .realImage(pilgrimage.getRealImage().getUrl())
                .address(pilgrimage.getDetailAddress())
                .latitude(pilgrimage.getLatitude())
                .longitude(pilgrimage.getLongitude())
                .isWritable(false)
                .isMultiWritable(false)
                .build();
    }

    // 내 성지순례 (비회원)
    public static PilgrimageDto.MyPilgrimageDto toMyPilgrimageDto() {
        return PilgrimageDto.MyPilgrimageDto.builder()
                .likedRallySize(0L)
                .likedRally(new ArrayList<>())
                .guestBookSize(0L)
                .guestBook(new ArrayList<>())
                .build();
    }

    // 내 성지순례 (회원)
    public static PilgrimageDto.MyPilgrimageDto toMyPilgrimageDto(
            List<PilgrimageDto.LikedRallyDto> likedRallyDtos, List<PilgrimageDto.MyGuestBookDto> myGuestBookDtos) {
        return PilgrimageDto.MyPilgrimageDto.builder()
                .likedRallySize(Long.valueOf(likedRallyDtos.size()))
                .likedRally(likedRallyDtos)
                .guestBookSize(Long.valueOf(myGuestBookDtos.size()))
                .guestBook(myGuestBookDtos)
                .build();
    }

    public static PilgrimageDto.LikedRallyDto toLikedRallyDto(Rally rally){
        return PilgrimageDto.LikedRallyDto.builder()
                .id(rally.getId())
                .name(rally.getName())
                .image(rally.getImage().getUrl())
                .build();
    }

    // 날짜 규격대로 변경 필요함
    public static PilgrimageDto.MyGuestBookDto toMyGuestBookDto(GuestBook guestBook, Image mainImg, List<String> hashTags){
        return PilgrimageDto.MyGuestBookDto.builder()
                .id(guestBook.getId())
                .title(guestBook.getTitle())
                .createdAt(guestBook.getCreatedAt().toString())
                .image(mainImg.getUrl())
                .hashTag(hashTags)
                .build();
    }

    public static PilgrimageDto.PilgrimageCategoryRegionDto toPilgrimageCategoryRegionDto(String state, List<PilgrimageDto.PilgrimageAddressDetailDto> dtos){
        return PilgrimageDto.PilgrimageCategoryRegionDto.builder()
                .state(state)
                .detail(dtos)
                .build();
    }

    public static PilgrimageDto.PilgrimageAddressDetailDto toPilgrimageAddressDetailDto(Address address){
        return PilgrimageDto.PilgrimageAddressDetailDto.builder()
                .id(address.getId())
                .district(address.getDistrict())
                .build();
    }
}
