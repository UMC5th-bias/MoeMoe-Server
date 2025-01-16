package com.favoriteplace.app.notification.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FcmMessage {
    private final boolean validateOnly;
    private final Message message;

    private FcmMessage(Message message){
        this.validateOnly = false;
        this.message = message;
    }

    @Getter
    @Builder
    public static class Message{
        private Notification notification;
        private String token;
    }

    @Getter
    @Builder
    public static class Notification{
        private String title;
        private String body;
        private String image;
    }
}
