package com.favoriteplace.global.util;


import com.favoriteplace.app.item.repository.ItemRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final ItemRepository itemRepository;

    /**
     * 일주일이 지난 NEW 카테고리에 있는 상품들은 NORMAL 카테고리로 이동
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul") //매일 자정 실행
    @Transactional
    public void run() {
        LocalDateTime now = LocalDateTime.now();
        itemRepository.changeCategory(now.minusDays(7));
    }
}
