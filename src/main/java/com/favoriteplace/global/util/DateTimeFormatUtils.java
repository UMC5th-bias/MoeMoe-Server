package com.favoriteplace.global.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeFormatUtils {

    public static String getPassDateTime(LocalDateTime dateTime){
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(dateTime, now);
        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        if(minutes <60){
            return minutes + "분 전";
        }
        else if(hours < 24){
            return hours + "시간 전";
        }
        else if(days < 7){
            return days + "시간 전";
        }
        else{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M월 d일");
            return dateTime.format(formatter);
        }
    }
}
