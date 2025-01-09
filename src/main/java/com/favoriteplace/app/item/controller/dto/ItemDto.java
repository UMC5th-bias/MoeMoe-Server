package com.favoriteplace.app.item.controller.dto;

import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.item.domain.Item;
import com.favoriteplace.app.member.controller.dto.MemberDto;
import com.favoriteplace.app.member.controller.dto.MemberDto.MemberInfo;
import com.favoriteplace.global.util.DateTimeFormatUtils;
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
    public static class ItemDetailResDto {
        private Integer userPoint;
        private String category;
        private String imageUrl;
        private String imageCenterUrl;
        private String saleDeadline;
        private String status;
        private String name;
        private int point;
        private String description;
        private Boolean alreadyBought;

        public static ItemDetailResDto from(Item item, Member member, Boolean alreadyBought) {
            return ItemDetailResDto.builder()
                .userPoint(member == null ? 0 : Long.valueOf(member.getPoint()).intValue())
                .category(item.getCategory().getName())
                .imageUrl(item.getDefaultImage().getUrl())
                .imageCenterUrl(item.getCenterImage() == null ? null : item.getCenterImage().getUrl())
                .saleDeadline(item.getSaleDeadline() == null ? null : DateTimeFormatUtils.convertDateToString(item.getSaleDeadline()))
                .status(item.getStatus().toString())
                .name(item.getName())
                .point(Long.valueOf(item.getPoint()).intValue())
                .description(item.getDescription())
                .alreadyBought(alreadyBought)
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
        private int point;
    }

    @Getter
    public static class ItemPurchaseRes {
        private Boolean canBuy;

        public ItemPurchaseRes(Boolean canBuy) {
            this.canBuy = canBuy;
        }
    }

}
