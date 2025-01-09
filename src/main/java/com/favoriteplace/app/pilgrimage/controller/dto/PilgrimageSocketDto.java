package com.favoriteplace.app.pilgrimage.controller.dto;

import lombok.*;

import java.util.Objects;

public class PilgrimageSocketDto {
    @Data
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ButtonState {
        private Boolean certifyButtonEnabled;
        private Boolean guestbookButtonEnabled;
        private Boolean multiGuestbookButtonEnabled;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ButtonState that = (ButtonState) o;
            return Objects.equals(certifyButtonEnabled, that.certifyButtonEnabled) &&
                    Objects.equals(guestbookButtonEnabled, that.guestbookButtonEnabled) &&
                    Objects.equals(multiGuestbookButtonEnabled, that.multiGuestbookButtonEnabled);
        }

        @Override
        public int hashCode() {
            return Objects.hash(certifyButtonEnabled, guestbookButtonEnabled, multiGuestbookButtonEnabled);
        }
    }

    @Data
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CertifyMessageDto {
        private Boolean success;
        private Boolean isComplete;
        private String message;
    }
}
