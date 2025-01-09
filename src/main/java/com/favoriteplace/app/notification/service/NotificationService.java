package com.favoriteplace.app.notification.service;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.notification.domain.Notification;
import com.favoriteplace.app.notification.controller.dto.NotificationResponseDto;
import com.favoriteplace.app.notification.repository.NotificationRepository;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.favoriteplace.app.notification.converter.NotificationConverter.toNotificationResponseDto;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Transactional
    public void readAllNotification(Member member) {
        notificationRepository.readAllNotification(member.getId());
    }

    @Transactional
    public void readNotification(Long notificationId, Member member) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RestApiException(ErrorCode.NOTIFICATION_NOT_EXIST));
        if (member != notification.getMember()) {
            throw new RestApiException(ErrorCode.NOTIFICATION_NOT_BELONG);
        }
        notification.readNotification();
    }

    @Transactional
    public void deleteNotification(Long notificationId, Member member) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RestApiException(ErrorCode.NOTIFICATION_NOT_EXIST));
        if (member != notification.getMember()) {
            throw new RestApiException(ErrorCode.NOTIFICATION_NOT_BELONG);
        }
        notificationRepository.delete(notification);
    }

    @Transactional
    public NotificationResponseDto getAllNotification(Member member, Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Notification> notifications = notificationRepository.findByMemberId(member.getId(), pageRequest);
        return toNotificationResponseDto(notifications);
    }
}
