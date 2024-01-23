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

        //TODO 추후 리팩토링 필수..(겹치는 로직(title/icon) 메소드로 따로 빼기)

        //한정 판매 아이템 목록 조회
        List<Item> limitedItemList = itemRepository.findAllByStatus(SaleStatus.LIMITED_SALE);
        List<ItemCategory> itemCategories = Arrays.stream(ItemCategory.values())
            .collect(Collectors.toList());

        List<Item> titleItems = limitedItemList.stream()
                                    .filter(item -> item.getType() == ItemType.TITLE)
                                    .collect(Collectors.toList());

        if (titleItems.size() != 0) {

            List<List<Item>> titleCollect = itemCategories.stream()
                .map(id -> titleItems.stream()
                    .filter(item -> item.getCategory() == id)
                    .collect(Collectors.toList()))
                .collect(Collectors.toList());

            titles = titleCollect.stream()
                .filter(title -> !title.isEmpty())
                .map(titleitemList -> ShopConverter.itemListDivideByCategory(titleitemList)).collect(
                    Collectors.toList());
        } else {
            titles = null;
        }

        List<Item> iconItems = limitedItemList.stream()
                                    .filter(item -> item.getType() == ItemType.ICON)
                                    .collect(Collectors.toList());

        if(iconItems.size() != 0) {
            List<List<Item>> iconCollect = itemCategories.stream()
                .map(id -> iconItems.stream()
                    .filter(item -> item.getCategory() == id)
                    .collect(Collectors.toList()))
                .collect(Collectors.toList());

            icons = iconCollect.stream()
                .filter(icon -> !icon.isEmpty())
                .map(titleIconList -> ShopConverter.itemListDivideByCategory(titleIconList)).collect(
                    Collectors.toList());
        } else {
            icons = null;
        }

        if (member == null) {
            return ItemDto.ItemListResDto.builder()
                .isLoggedIn(false)
                .userInfo(null)
                .titles(titles)
                .icons(icons)
                .build();
        }
        return null;
    }

}
