package com.favoriteplace.app.converter;

import com.favoriteplace.app.domain.item.Item;
import com.favoriteplace.app.dto.item.ItemDto;
import com.favoriteplace.app.dto.item.ItemDto.ItemList;
import java.util.List;
import java.util.stream.Collectors;

public class ShopConverter {

    public static ItemDto.ItemList itemList(Item item) {
        return ItemDto.ItemList.builder()
            .id(item.getId().intValue())
            .name(item.getName())
            .imageUrl(item.getImage().getUrl())
            .point(item.getPoint().intValue())
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

}
