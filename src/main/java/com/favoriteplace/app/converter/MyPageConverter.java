package com.favoriteplace.app.converter;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.item.Item;
import com.favoriteplace.app.dto.MyPageDto;

import java.util.List;

public class MyPageConverter {
    public static MyPageDto.MyProfileDto toMyProfileDto (Member member){
        return MyPageDto.MyProfileDto.builder()
                .nickname(member.getNickname())
                .introduction(member.getDescription())
                .point(member.getPoint())
                .profileImg(member.getProfileImageUrl())
                .userTitleImg(member.getProfileTitle()==null?null
                        :member.getProfileTitle().getImage().getUrl())
                .userIconImg(member.getProfileIcon()==null?null
                        :member.getProfileIcon().getImage().getUrl())
                .email(member.getEmail())
                .build();
    }

    public static MyPageDto.MyItemDetailDto toMyItemDetailDto(Item item, Boolean isWear){
        return MyPageDto.MyItemDetailDto.builder()
                .id(item.getId())
                .imageUrl(item.getImage().getUrl())
                .isWear(isWear)
                .build();
    }

    public static MyPageDto.MyItemDto toMyItemDto(List<MyPageDto.MyItemDetailDto> limited,
                                                  List<MyPageDto.MyItemDetailDto> always,
                                                  List<MyPageDto.MyItemDetailDto> pilgrimage) {
        return MyPageDto.MyItemDto.builder()
                .limited(limited)
                .always(always)
                .pilgrimage(pilgrimage)
                .build();
    }
}
