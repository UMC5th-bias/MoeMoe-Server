package com.favoriteplace.app.item.converter;

import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.item.domain.Item;
import com.favoriteplace.app.item.controller.dto.ItemDto;
import com.favoriteplace.app.item.controller.dto.ItemDto.ItemList;
import com.favoriteplace.app.item.controller.dto.ItemDto.ItemListDivideByCategory;
import com.favoriteplace.app.item.controller.dto.ItemDto.ItemListDivideBySaleStatus;
import com.favoriteplace.app.item.controller.dto.ItemDto.ItemListResDto;
import com.favoriteplace.app.item.controller.dto.ItemDto.NewItemListResDto;
import com.favoriteplace.app.member.controller.dto.MemberDto;
import com.favoriteplace.app.member.controller.dto.MemberDto.MemberInfo;

import java.util.List;
import java.util.stream.Collectors;

public class ShopConverter {

    public static ItemDto.ItemList itemList(Item item) {
        return ItemDto.ItemList.builder()
                .id(item.getId().intValue())
                .name(item.getName())
                .imageUrl(item.getDefaultImage().getUrl())
                .point(Long.valueOf(item.getPoint()).intValue())
                .build();
    }

    public static ItemDto.ItemListDivideByCategory itemListDivideByCategory(List<Item> items) {
        List<ItemList> itemLists = items.stream().map(ShopConverter::itemList)
                .collect(Collectors.toList());

        return ItemDto.ItemListDivideByCategory.builder()
                .category(items.get(0).getCategory().getName())
                .itemList(itemLists)
                .build();
    }

    public static ItemDto.ItemListDivideBySaleStatus itemListDivideByStatus(List<Item> items) {
        List<ItemList> itemLists = items.stream().map(ShopConverter::itemList)
                .collect(Collectors.toList());

        return ItemDto.ItemListDivideBySaleStatus.builder()
                .status(items.get(0).getStatus().toString())
                .itemList(itemLists)
                .build();
    }

    public static ItemDto.ItemListResDto totalItemList(
            Member member, List<ItemListDivideByCategory> titles, List<ItemListDivideByCategory> icons
    ) {
        MemberDto.MemberInfo memberDto = null;
        if (member != null) {
            memberDto = MemberInfo.from(member);
        }
        return ItemListResDto.from(memberDto, titles, icons);
    }

    public static ItemDto.NewItemListResDto totalNewItemList(
            List<ItemListDivideBySaleStatus> titles, List<ItemListDivideBySaleStatus> icons
    ) {
        return NewItemListResDto.from(titles, icons);
    }


}
