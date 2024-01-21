package com.favoriteplace.app.dto.travel;

import lombok.*;

import java.util.ArrayList;
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
        List<LikedRallyDto> likedRally = new ArrayList<>();
        Long guestBookSize;
        List<MyGuestBookDto> guestBook = new ArrayList<>();
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
        List<String> hashTag = new ArrayList<>();
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PilgrimageCategoryRegionDto {
        String state;
        List<PilgrimageAddressDetailDto> detail = new ArrayList<>();
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
