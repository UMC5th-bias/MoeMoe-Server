package com.favoriteplace.app.service;

import static com.favoriteplace.global.exception.ErrorCode.ITEM_NOT_EXISTS;

import com.favoriteplace.app.converter.ShopConverter;
import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.enums.ItemCategory;
import com.favoriteplace.app.domain.enums.ItemType;
import com.favoriteplace.app.domain.enums.SaleStatus;
import com.favoriteplace.app.domain.item.AcquiredItem;
import com.favoriteplace.app.domain.item.Item;
import com.favoriteplace.app.dto.item.ItemDto;
import com.favoriteplace.app.dto.item.ItemDto.ItemDetailResDto;
import com.favoriteplace.app.dto.item.ItemDto.ItemListDivideByCategory;
import com.favoriteplace.app.dto.item.ItemDto.ItemListDivideBySaleStatus;
import com.favoriteplace.app.dto.item.ItemDto.ItemPurchaseRes;
import com.favoriteplace.app.repository.AcquiredItemRepository;
import com.favoriteplace.app.repository.ItemRepository;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.util.SecurityUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ShopService {
    private final SecurityUtil securityUtil;
    private final ItemRepository itemRepository;
    private final AcquiredItemRepository acquiredItemRepository;

    @Transactional(readOnly = true)
    public ItemDto.ItemDetailResDto getItemDetail(HttpServletRequest request, Long itemId) {
        Member member = securityUtil.getUserFromHeader(request);

        Item item = itemRepository.findAllByIdWithImage(itemId)
            .orElseThrow(() -> new RestApiException(ITEM_NOT_EXISTS));

        Boolean alreadyBought = acquiredItemRepository.findByMemberAndItem(member, item)
            .isPresent();

        return ItemDetailResDto.from(item, member, alreadyBought);

    }

    @Transactional(readOnly = true)
    public ItemDto.ItemListResDto getLimitedProduct(HttpServletRequest request) {
        Member member = securityUtil.getUserFromHeader(request);

        List<ItemListDivideByCategory> titles = null;
        List<ItemListDivideByCategory> icons = null;
        List<ItemCategory> itemCategories;

        //TODO 추후 리팩토링하면 좋을 것 같은 부분

        //한정 판매 아이템 목록 & 현존하는 ItemCategory 조회
        List<Item> limitedItemList = itemRepository.findAllByStatus(SaleStatus.LIMITED_SALE);
        itemCategories = getItemCategories();

        List<Item> titleItems = getItemsByItemType(limitedItemList, ItemType.TITLE); //한정판매 + 칭호

        if (titleItems.size() != 0) {
            titles = getItemListDivideByCategory(titleItems, itemCategories);
        }

        List<Item> iconItems = getItemsByItemType(limitedItemList, ItemType.ICON); //한정판매 + 아이콘

        if(iconItems.size() != 0) {
            icons = getItemListDivideByCategory(iconItems, itemCategories);
        }

        return ShopConverter.totalItemList(member, titles, icons);
    }

    @Transactional(readOnly = true)
    public ItemDto.ItemListResDto getAlwaysSellProduct(HttpServletRequest request) {
        Member member = securityUtil.getUserFromHeader(request);

        List<ItemListDivideByCategory> titles = null;
        List<ItemListDivideByCategory> icons = null;
        List<ItemCategory> itemCategories;

        //TODO 추후 리팩토링하면 좋을 것 같은 부분

        //한정 판매 아이템 목록 & 현존하는 ItemCategory 조회
        List<Item> limitedItemList = itemRepository.findAllByStatus(SaleStatus.ALWAYS_ON_SALE);
        itemCategories = getItemCategories();

        List<Item> titleItems = getItemsByItemType(limitedItemList, ItemType.TITLE);

        if (titleItems.size() != 0) {
            titles = getItemListDivideByCategory(titleItems, itemCategories);
        }

        List<Item> iconItems = getItemsByItemType(limitedItemList, ItemType.ICON);

        if(iconItems.size() != 0) {
            icons = getItemListDivideByCategory(iconItems, itemCategories);
        }

        return ShopConverter.totalItemList(member, titles, icons);
    }

    @Transactional(readOnly = true)
    public ItemDto.NewItemListResDto getNewItemList() {
        LocalDateTime now = LocalDateTime.now();
        List<Item> titleItemList = itemRepository.findAllByNEWCategory(ItemType.TITLE, now.minusDays(7));
        List<Item> iconItemList = itemRepository.findAllByNEWCategory(ItemType.ICON, now.minusDays(7));

        List<ItemListDivideBySaleStatus> titles = null;
        List<ItemListDivideBySaleStatus> icons = null;

        List<SaleStatus> saleStatusList = new ArrayList<>(Arrays.asList(SaleStatus.LIMITED_SALE, SaleStatus.ALWAYS_ON_SALE));

        titles = getItemListDivideBySaleStatus(titleItemList, saleStatusList);
        icons = getItemListDivideBySaleStatus(iconItemList, saleStatusList);

        return ShopConverter.totalNewItemList(titles, icons);

    }

    public ItemDto.ItemPurchaseRes buyItem(Long itemId) {
        Boolean canBuy;
        Member member = securityUtil.getUser();
        if (member == null) {
            throw new RestApiException(ErrorCode.USER_NOT_FOUND);
        }

        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RestApiException(ITEM_NOT_EXISTS));

        canBuy = member.getPoint() >= item.getPoint() ? true : false;
        if (canBuy) {
            member.updatePointWhenBuyItem(member.getPoint() - item.getPoint());
        }

        //acquiredItem 테이블(보유 아이템) 저장
        AcquiredItem newAcquiredItem =
            AcquiredItem.builder()
                .item(item)
                .member(member)
                .build();

        acquiredItemRepository.save(newAcquiredItem);
        return new ItemPurchaseRes(canBuy);
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

    //SaleStauts별 아이템 조회
    public List<ItemListDivideBySaleStatus> getItemListDivideBySaleStatus(List<Item> itemList, List<SaleStatus> saleStatusList) {
        if (itemList.isEmpty()) {
            return null;
        }
        List<List<Item>> collect = saleStatusList.stream()
            .map(id -> itemList.stream()
                .filter(item -> item.getStatus() == id)
                .collect(Collectors.toList()))
            .collect(Collectors.toList());

        return collect.stream()
            .filter(list -> !list.isEmpty())
            .map(items -> ShopConverter.itemListDivideByStatus(items)).collect(
                Collectors.toList());
    }
}
