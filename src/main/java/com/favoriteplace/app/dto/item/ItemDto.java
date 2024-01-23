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
        private List<ItemListDivideByCategory> titles;
        private List<ItemListDivideByCategory> icons;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class ItemListDivideByCategory {
        private String category;
        private List<ItemList> itemList;

    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class ItemList {
        private Integer id;
        private String name;
        private String imageUrl;
        private Integer point;
    }

}
