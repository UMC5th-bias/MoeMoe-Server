package com.favoriteplace.app.controller;

import com.favoriteplace.app.service.fcm.FCMNotificationService;
import com.favoriteplace.app.service.fcm.dto.PostTokenCond;
import com.favoriteplace.app.service.fcm.enums.TokenMessage;
import com.favoriteplace.app.service.fcm.enums.TotalTopicMessage;

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
