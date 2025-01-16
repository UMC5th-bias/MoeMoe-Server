package com.favoriteplace.app.controller;

import com.favoriteplace.app.dto.item.ItemDto;
import com.favoriteplace.app.service.ShopService;
import com.favoriteplace.global.auth.resolver.UserEmail;
import com.favoriteplace.global.util.SecurityUtil;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shop")
public class ShopController {
    private final ShopService shopService;
    private final SecurityUtil securityUtil;

    @GetMapping("/limited")
    public ResponseEntity<ItemDto.ItemListResDto> getLimitedProduct(HttpServletRequest request) {
        return ResponseEntity.ok(shopService.getLimitedProduct(request));
    }

    @GetMapping("/always")
    public ResponseEntity<ItemDto.ItemListResDto> getAlwaysSellProduct(HttpServletRequest request) {
        return ResponseEntity.ok(shopService.getAlwaysSellProduct(request));
    }

    @GetMapping("/new")
    public ResponseEntity<ItemDto.NewItemListResDto> getNewItemList(@UserEmail String email) {
        System.out.println("email = " + email);
        return ResponseEntity.ok(shopService.getNewItemList());
    }

    @GetMapping("/detail/{item_id}")
    public ResponseEntity<ItemDto.ItemDetailResDto> getItemDetail(
            HttpServletRequest request, @PathVariable("item_id") Long itemId
    ) {
        return ResponseEntity.ok(shopService.getItemDetail(request, itemId));
    }

    @PostMapping("/purchase/{item_id}")
    public ResponseEntity<ItemDto.ItemPurchaseRes> buyItem(
            @PathVariable("item_id") Long itemId,
            @UserEmail String userEmail
    ) {
        return ResponseEntity.ok(shopService.buyItem(itemId));
    }
}
