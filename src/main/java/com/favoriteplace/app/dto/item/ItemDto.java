package com.favoriteplace.app.dto.item;

import com.favoriteplace.app.dto.member.MemberDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class ItemDto {
    @Builder
    @Getter
    @AllArgsConstructor
    public static class ItemListResDto {
        private Boolean isLoggedIn;
        private MemberDto.MemberInfo userInfo;
        private List<itemListDivideByCategory> titles;
        private List<itemListDivideByCategory> icons;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class itemListDivideByCategory {
        private String category;
        private List<itemList> itemList;

    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class itemList {
        private Integer id;
        private String name;
        private String imageUrl;
        private Integer point;
    }

}
