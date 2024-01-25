package com.favoriteplace.app.service;

import com.favoriteplace.app.converter.ShopConverter;
import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.enums.ItemCategory;
import com.favoriteplace.app.domain.enums.ItemType;
import com.favoriteplace.app.domain.enums.SaleStatus;
import com.favoriteplace.app.domain.item.Item;
import com.favoriteplace.app.dto.item.ItemDto;
import com.favoriteplace.app.dto.item.ItemDto.ItemListDivideByCategory;
import com.favoriteplace.app.repository.ItemRepository;
import com.favoriteplace.global.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopService {
    private final SecurityUtil securityUtil;
    private final ItemRepository itemRepository;

    public ItemDto.ItemListResDto getLimitedProduct(HttpServletRequest request) {
        Member member = securityUtil.getUserFromHeader(request);

        List<ItemListDivideByCategory> titles;
        List<ItemListDivideByCategory> icons;
        List<ItemCategory> itemCategories;

        //TODO 추후 리팩토링하면 좋을 것 같은 부분

        //한정 판매 아이템 목록 & 현존하는 ItemCategory 조회
        List<Item> limitedItemList = itemRepository.findAllByStatus(SaleStatus.LIMITED_SALE);
        itemCategories = Arrays.stream(ItemCategory.values())
            .collect(Collectors.toList());

        List<Item> titleItems = getItemsByItemType(limitedItemList, ItemType.TITLE);

        if (titleItems.size() != 0) {
            titles = getItemListDivideByCategory(titleItems, itemCategories);
        } else {
            titles = null;
        }

        List<Item> iconItems = getItemsByItemType(limitedItemList, ItemType.ICON);

        if(iconItems.size() != 0) {
            icons = getItemListDivideByCategory(iconItems, itemCategories);
        } else {
            icons = null;
        }

        return ShopConverter.totalItemList(member, titles, icons);
    }

    public ItemDto.ItemListResDto getAlwaysSellProduct(HttpServletRequest request) {
        Member member = securityUtil.getUserFromHeader(request);

        List<ItemListDivideByCategory> titles;
        List<ItemListDivideByCategory> icons;
        List<ItemCategory> itemCategories;

        //TODO 추후 리팩토링하면 좋을 것 같은 부분

        //한정 판매 아이템 목록 & 현존하는 ItemCategory 조회
        List<Item> limitedItemList = itemRepository.findAllByStatus(SaleStatus.ALWAYS_ON_SALE);
        itemCategories = Arrays.stream(ItemCategory.values())
            .collect(Collectors.toList());

        List<Item> titleItems = getItemsByItemType(limitedItemList, ItemType.TITLE);

        if (titleItems.size() != 0) {
            titles = getItemListDivideByCategory(titleItems, itemCategories);
        } else {
            titles = null;
        }

        List<Item> iconItems = getItemsByItemType(limitedItemList, ItemType.ICON);

        if(iconItems.size() != 0) {
            icons = getItemListDivideByCategory(iconItems, itemCategories);
        } else {
            icons = null;
        }

        return ShopConverter.totalItemList(member, titles, icons);
    }

    public List<ItemCategory> getItemCategories() {
        return Arrays.stream(ItemCategory.values())
            .collect(Collectors.toList());
    }



    //칭호, 아이콘 별 아이템 조회
    public List<Item> getItemsByItemType(List<Item> itemList, ItemType itemType) {
        return itemList.stream()
            .filter(item -> item.getType() == itemType)
            .collect(Collectors.toList());
    }

    //카테고리별 아이템 조회
    public List<ItemListDivideByCategory> getItemListDivideByCategory(List<Item> itemList, List<ItemCategory> itemCategories) {

        List<List<Item>> collect = itemCategories.stream()
            .map(id -> itemList.stream()
                .filter(item -> item.getCategory() == id)
                .collect(Collectors.toList()))
            .collect(Collectors.toList());

        return collect.stream()
            .filter(title -> !title.isEmpty())
            .map(titleitemList -> ShopConverter.itemListDivideByCategory(titleitemList)).collect(
                Collectors.toList());
    }
}
