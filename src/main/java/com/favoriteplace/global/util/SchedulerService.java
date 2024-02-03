package com.favoriteplace.global.util;


import com.favoriteplace.app.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SchedulerService {
    private final ItemRepository itemRepository;

    /**
     * 일주일이 지난 NEW 카테고리에 있는 상품들은 NORMAL 카테고리로 이동
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul") //매일 자정 실행
    public void run() {
        itemRepository.changeCategory();
    }
}
