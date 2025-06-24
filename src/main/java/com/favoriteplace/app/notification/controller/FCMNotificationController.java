package com.favoriteplace.app.notification.controller;

import com.favoriteplace.app.notification.service.FCMNotificationService;
import com.favoriteplace.app.notification.controller.dto.PostTokenCond;
import com.favoriteplace.app.notification.service.TokenMessage;
import com.favoriteplace.app.notification.service.TotalTopicMessage;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnProperty(prefix = "fcm", name = "enabled", havingValue = "true")
@RequestMapping("/fcm")
@RequiredArgsConstructor
public class FCMNotificationController {
    private final FCMNotificationService fcmNotificationService;

    @PostMapping("/token")
    public String sendNotificationByToken(
            @RequestParam String token
    ) {
        return fcmNotificationService.sendNotificationByToken(PostTokenCond.builder()
                .token(token).postId(1L).tokenMessage(TokenMessage.POST_NEW_COMMENT).message("댓글 내용")
                .build());
    }

    @PostMapping("/topic/subscribe")
    public String subScribeTopic(
            @RequestParam String token
    ) {
        fcmNotificationService.subscribeTopic("total", token);
        return "토픽에 정상적으로 등록 완료";
    }

    @PostMapping("/topic/send")
    public String sendAlarmByTopic(

    ) {
        return fcmNotificationService.sendTotalAlarmByTopic(TotalTopicMessage.INFORM);
    }
}
