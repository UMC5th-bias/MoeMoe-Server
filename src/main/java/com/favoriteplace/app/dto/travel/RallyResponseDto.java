package com.favoriteplace.app.dto.travel;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;


public class RallyResponseDto {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RallyDetailResponseDto {
        String name;
        Long pilgrimageNumber;
        Long myPilgrimageNumber;
        String image;
        String description;
        Long achieveNumber;
        String itemImage;
        Boolean isLike;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RallyAddressListDto {
        String name;
        Long pilgrimageNumber;
        Long myPilgrimageNumber;
        String image;
        List<RallyAddressDto> rally = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RallyAddressDto {
        String address;
        List<RallyAddressPilgrimageDto> pilgrimage = new ArrayList<>();
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RallyAddressPilgrimageDto {
        Long id;
        String detailAddress;
        String image;
        Boolean isVisited;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RallyTrendingDto {
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
    public static class PilgrimageCategoryAnimeDto {
        Long id;
        String name;
        Long pilgrimageNumber;
        Long myPilgrimageNumber;
        String image;
    }

    @Builder
    @Getter
    @NoArgsConstructor(access = PROTECTED)
    @AllArgsConstructor(access = PRIVATE)
    public static class SearchAnimeDto {
        String name;
        Long pilgrimageNumber;
        Long myPilgrimageNumber;
        String image;
    }

    @Builder
    @Getter
    @NoArgsConstructor(access = PROTECTED)
    @AllArgsConstructor(access = PRIVATE)
    static public class SearchRegionDto {
        String address;
        List<SearchRegionDetailDto> rallies;
    }

    @Builder
    @Getter
    @NoArgsConstructor(access = PROTECTED)
    @AllArgsConstructor(access = PRIVATE)
    public static class SearchRegionDetailDto {
        String name;
        String image;
        String detailAddress;
    }
}
