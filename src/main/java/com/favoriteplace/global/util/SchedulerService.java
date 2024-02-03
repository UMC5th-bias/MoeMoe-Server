package com.favoriteplace.global.util;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SchedulerService {

    /**
     * 일주일이 지난 NEW 카테고리에 있는 상품들은 NORMAL 카테고리로 이동
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul") //매일 자정 실행
    public void run() {

    }
}
