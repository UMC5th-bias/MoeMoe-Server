package com.favoriteplace.app.controller;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.service.NotificationService;
import com.favoriteplace.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final SecurityUtil securityUtil;

    // 특정 알림 읽음 처리
    @PatchMapping("/{notificationId}")
    public ResponseEntity<?> readNotification(
            @PathVariable Long notificationId
    ){
        Member member = securityUtil.getUser();
        notificationService.readNotification(notificationId, member);
        return ResponseEntity.noContent().build();
    }

    // 알림 상세 페이지
//    @GetMapping()
//    public ResponseEntity<?> getAllNotification(
//            @RequestParam Long page
//    ){
//
//        //return ResponseEntity.ok();
//    }
//
    // 알림 한번에 다 읽음 처리
//    @PostMapping()
//    public ResponseEntity<?> addNotification(
//            @RequestBody
//    ){
//
//    }

}
