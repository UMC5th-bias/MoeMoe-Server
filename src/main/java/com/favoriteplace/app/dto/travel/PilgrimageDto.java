package com.favoriteplace.app.dto.travel;

import lombok.*;

import java.util.List;

public class PilgrimageDto {
    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PilgrimageDetailDto {
        String rallyName;
        Long pilgrimageNumber;
        Long myPilgrimageNumber;
        String image;
        String realImage;
        String address;
        Double latitude;
        Double longitude;
        Boolean isWritable;
        Boolean isMultiWritable;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MyPilgrimageDto {
        Long likedRallySize;
        List<LikedRallyDto> likedRally;
        Long guestBookSize;
        List<MyGuestBookDto> guestBook;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LikedRallyDto {
        Long id;
        String name;
        String image;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MyGuestBookDto {
        Long id;
        String title;
        String createdAt;
        String image;
        Long imageSize;
        List<String> hashTag;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PilgrimageCategoryAnimeDto {
        Long id;
        String name;
        Long pilgrimageNumber;
        Long myPilgrimageNumber;
        String image;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PilgrimageCategoryRegionDto {
        String state;
        List<PilgrimageAddressDetailDto> detail;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PilgrimageAddressDetailDto {
        Long id;
        String district;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PilgrimageCategoryRegionDetailDto {
        Long id;
        String title;
        String detailAddress;
        Double latitude;
        Double longitude;
    }
}
