package com.favoriteplace.app.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SaleStatus {
    FOR_SALE(0, "파는 상품"),
    NOT_FOR_SALE(1, "팔지 않는 상품"),
    LIMITED_SALE(2, "한정 판매 상품"),
    ALWAYS_ON_SALE(3, "상시 판매 상품");

    private final Integer num;
    private final String description;
}
