package com.favoriteplace.global.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeFormatUtils {

    //datetime : 글 작성 시간 -> 분/시간/일/날짜
    public static String getPassDateTime(LocalDateTime dateTime){
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(dateTime, now);
        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        if(minutes < 60){
            return minutes + "분 전";
        }
        else if(hours < 24){
            return hours + "시간 전";
        }
        else if(days < 7){
            return days + "일 전";
        }
        else{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            return dateTime.format(formatter);
        }
    }

    //dateTime -> "yyyy.MM.dd"
    public static String convertDateToString(LocalDateTime dateTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return dateTime.format(formatter);
    }

}
