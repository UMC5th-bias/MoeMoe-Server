package com.favoriteplace.app.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FCMNotificationService {
    private final FirebaseMessaging firebaseMessaging;

    public String sendNotificationByToken(String token) {
        try{
            Message message = makeMessage(token);
            return firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            log.warn("fcm: {}", e.getErrorCode());
            log.warn("fcm : {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private Message makeMessage(String token){
        Notification notification = new Notification("test 제목", "test 잘 동작하는지 확인");
        Message message = Message.builder()
                .setNotification(notification)
                .setToken(token)
                .build();
        return message;
    }

}
