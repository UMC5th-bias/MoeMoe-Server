package com.favoriteplace.app.converter;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.item.Item;
import com.favoriteplace.app.dto.MyPageDto;

public class MyPageConverter {
    public static MyPageDto.MyProfileDto toMyProfileDto (Member member){
        return MyPageDto.MyProfileDto.builder()
                .nickname(member.getNickname())
                .introduction(member.getDescription())
                .point(member.getPoint())
                .profileImg(member.getProfileImageUrl())
                .userTitleImg(member.getProfileTitle()==null?null:member.getProfileTitle().getImage().getUrl())
                .userIconImg(member.getProfileIcon()==null?null:member.getProfileIcon().getImage().getUrl())
                .email(member.getEmail())
                .build();
    }
}
