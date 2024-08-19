package com.favoriteplace.app.service.fcm;

import com.favoriteplace.app.service.fcm.enums.TokenMessage;
import com.favoriteplace.app.service.fcm.enums.TotalTopicMessage;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.TopicManagementResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FCMNotificationService {
    private final FirebaseMessaging firebaseMessaging;

    /**
     * token - 단일 기기
     */
    @Transactional
    public String sendNotificationByToken(String token, Long postId, TokenMessage tokenMessage) {
        try{
            Message message = makeTokenMessage(token, postId, tokenMessage);
            return firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            log.warn("fcm: {}", e.getErrorCode());
            log.warn("fcm : {}", e.getMessage());
            throw new RestApiException(ErrorCode.TOKEN_ALARM_NOT_SEND);
        }
    }

    /**
     * token - Message 제작
     */
    private Message makeTokenMessage(String token, Long postId, TokenMessage tokenMessage){
        Message message = Message.builder()
                .setToken(token)
                .putData("type", tokenMessage.getType())
                .putData("title", tokenMessage.getTitle())
                .putData("message", tokenMessage.getMessage())
                .putData("postId", postId.toString())
                //.putData("date", LocalDateTime.now().toString())
                .build();
        return message;
    }

    /**
     * topic 구독
     */
    @Transactional
    public void subscribeTopic(String topic, String token){
        try{
            List<String> tokens = Collections.singletonList(token);
            TopicManagementResponse response = FirebaseMessaging.getInstance().subscribeToTopic(tokens, topic);

        } catch (FirebaseMessagingException e){
            throw new RestApiException(ErrorCode.TOPIC_SUBSCRIBE_FAIL);
        }
    }

    /**
     * topic 구독 취소
     */
    @Transactional
    public void unsubscribeTopic(String topic, String token){
        try{
            List<String> tokens = Collections.singletonList(token);
            TopicManagementResponse response = FirebaseMessaging.getInstance().unsubscribeFromTopic(tokens, topic);

        } catch (FirebaseMessagingException e){
            throw new RestApiException(ErrorCode.TOPIC_UNSUBSCRIBE_FAIL);
        }
    }

    /**
     * topic 전송 - 애니메이션
     */
    @Transactional
    public String sendAnimationAlarmByTopic(Long animationId, String name) {
        try{
            Message message = makeAnimationTopicMessage(animationId, name);
            return firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            log.warn("fcm: {}", e.getErrorCode());
            log.warn("fcm : {}", e.getMessage());
            throw new RestApiException(ErrorCode.TOPIC_ALARM_NOT_SEND);
        }
    }

    /**
     * topic 전송 - 전체 알림
     */
    @Transactional
    public String sendTotalAlarmByTopic(TotalTopicMessage totalTopicMessage) {
        try{
            Message message = makeTotalTopicMessage(totalTopicMessage);
            return firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            log.warn("fcm: {}", e.getErrorCode());
            log.warn("fcm : {}", e.getMessage());
            throw new RestApiException(ErrorCode.TOPIC_ALARM_NOT_SEND);
        }
    }

    /**
     * topic - 애니메이션 Message 제작
     */
    private Message makeAnimationTopicMessage(Long animationId, String name){
        String topic = "animation" + animationId.toString();
        Message message = Message.builder()
                .setTopic(topic)
                .putData("type", "animation")
                .putData("title", String.format("애니메이션 %s의 성지순례가 추가되었습니다!", name))
                .putData("message", "지금 확인하러 가기")
                .putData("animationId", animationId.toString())
                //.putData("date", LocalDateTime.now().toString())
                .build();
        return message;
    }

    /**
     * topic - 홈화면 Message 제작
     */
    private Message makeTotalTopicMessage(TotalTopicMessage totalTopicMessage){
        Message message = Message.builder()
                .setTopic("total")
                .putData("type", totalTopicMessage.getType())
                .putData("title", totalTopicMessage.getTitle())
                .putData("message", totalTopicMessage.getMessage())
                //.putData("date", LocalDateTime.now().toString())
                .build();
        return message;
    }


}
