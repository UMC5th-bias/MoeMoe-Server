package com.favoriteplace.app.service;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.Notification;
import com.favoriteplace.app.repository.NotificationRepository;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Transactional
    public void readNotification(Long notificationId, Member member) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new RestApiException(ErrorCode.NOTIFICATION_NOT_EXIST));
        if(member != notification.getMember()){
            throw new RestApiException(ErrorCode.NOTIFICATION_NOT_BELONG);
        }
        notification.readNotification();
    }
}
