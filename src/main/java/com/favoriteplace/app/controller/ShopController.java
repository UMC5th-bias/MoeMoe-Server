package com.favoriteplace.app.controller;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.dto.item.ItemDto;
import com.favoriteplace.app.service.ShopService;
import com.favoriteplace.global.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shop")
public class ShopController {
    private final ShopService shopService;

    @GetMapping("/limited")
    public ResponseEntity<ItemDto.ItemListResDto> getLimitedProduct(HttpServletRequest request) {
        return ResponseEntity.ok(shopService.getLimitedProduct(request));
    }

    @GetMapping("/always")
    public ResponseEntity<ItemDto.ItemListResDto> getAlwaysSellProduct(HttpServletRequest request) {
        return ResponseEntity.ok(shopService.getAlwaysSellProduct(request));
    }
}
