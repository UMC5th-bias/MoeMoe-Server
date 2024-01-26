package com.favoriteplace.app.dto.item;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.item.Item;
import com.favoriteplace.app.dto.member.MemberDto;
import com.favoriteplace.app.dto.member.MemberDto.MemberInfo;
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

        public static ItemListResDto from(MemberInfo userInfo, List<ItemListDivideByCategory> titles, List<ItemListDivideByCategory> icons) {
            return ItemListResDto.builder()
                .isLoggedIn(userInfo == null ? false : true)
                .userInfo(userInfo)
                .titles(titles)
                .icons(icons)
                .build();
        }
    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class NewItemListResDto {
        private List<ItemListDivideBySaleStatus> titles;
        private List<ItemListDivideBySaleStatus> icons;

        public static NewItemListResDto from(List<ItemListDivideBySaleStatus> titles, List<ItemListDivideBySaleStatus> icons) {
            return NewItemListResDto.builder()
                .titles(titles)
                .icons(icons)
                .build();
        }
    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class ItemListDivideBySaleStatus {
        private String status;
        private List<ItemList> itemList;

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
