package com.favoriteplace.app.converter;

import com.favoriteplace.app.domain.travel.Pilgrimage;
import com.favoriteplace.app.dto.travel.PilgrimageDto;

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
}
