package com.favoriteplace.app.controller;

import com.favoriteplace.app.service.FCMNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fcm")
@RequiredArgsConstructor
public class FCMNotificationController {
    private final FCMNotificationService fcmNotificationService;

    @PostMapping("")
    public String sendNotificationByToken(
            @RequestParam String token
    ){
        return fcmNotificationService.sendNotificationByToken(token);
    }
}
