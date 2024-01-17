package com.favoriteplace.app.converter;

import com.favoriteplace.app.domain.travel.Pilgrimage;
import com.favoriteplace.app.domain.travel.Rally;
import com.favoriteplace.app.dto.travel.RallyDto;

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
}
