package com.favoriteplace.app.dto.travel;

import lombok.*;

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
}
