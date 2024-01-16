package com.favoriteplace.app.dto.travel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PilgrimageDto {
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PilgrimageDetailDto {
        String rallyName;
        Integer pilgrimageNumber;
        Integer myPilgrimageNumber;
        String image;
        String realImage;
        Double latitude;
        Double longitude;
    }
}
