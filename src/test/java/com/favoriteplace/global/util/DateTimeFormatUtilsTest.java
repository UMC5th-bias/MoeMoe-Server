package com.favoriteplace.global.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;


class DateTimeFormatUtilsTest {

    @Test
    void getPassDateTime() {
        //Given
        LocalDateTime dateTime = LocalDateTime.of(2024,1,10,10,36,00);

        //When
        String result = DateTimeFormatUtils.getPassDateTime(dateTime);

        //Then
        System.out.println(result);
    }
}