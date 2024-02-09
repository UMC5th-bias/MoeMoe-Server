package com.favoriteplace.app.dto.travel;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

public class PilgrimageDto {
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor(access = PROTECTED)
    @AllArgsConstructor(access = PRIVATE)
    public static class PilgrimageDetailDto {
        String rallyName;
        Long pilgrimageNumber;
        Long myPilgrimageNumber;
        String image;
        String realImage;
        String address;
        Double latitude;
        Double longitude;
        Boolean isCertified;
        Boolean isWritable;
        Boolean isMultiWritable;
    }

    @Builder
    @Getter
    @NoArgsConstructor(access = PROTECTED)
    @AllArgsConstructor(access = PRIVATE)
    public static class MyPilgrimageDto {
        Long likedRallySize;
        List<LikedRallyDto> likedRally = new ArrayList<>();
        Long guestBookSize;
        List<MyGuestBookDto> guestBook = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor(access = PROTECTED)
    @AllArgsConstructor(access = PRIVATE)
    public static class LikedRallyDto {
        Long id;
        String name;
        String image;
    }

    @Builder
    @Getter
    @NoArgsConstructor(access = PROTECTED)
    @AllArgsConstructor(access = PRIVATE)
    public static class MyGuestBookDto {
        Long id;
        String title;
        String createdAt;
        String image;
        List<String> hashTag = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor(access = PROTECTED)
    @AllArgsConstructor(access = PRIVATE)
    public static class PilgrimageCategoryRegionDto {
        String state;
        List<PilgrimageAddressDetailDto> detail;
    }

    @Builder
    @Getter
    @NoArgsConstructor(access = PROTECTED)
    @AllArgsConstructor(access = PRIVATE)
    public static class PilgrimageAddressDetailDto {
        Long id;
        String district;
    }

    @Builder
    @Getter
    @NoArgsConstructor(access = PROTECTED)
    @AllArgsConstructor(access = PRIVATE)
    public static class PilgrimageCategoryRegionDetailDto {
        Long id;
        String title;
        String detailAddress;
        String image;
        Double latitude;
        Double longitude;
    }

    @Getter
    @NoArgsConstructor(access = PROTECTED)
    @AllArgsConstructor(access = PRIVATE)
    public static class PilgrimageCertifyRequestDto{
        Long longitude;
        Long latitude;
    }
}
