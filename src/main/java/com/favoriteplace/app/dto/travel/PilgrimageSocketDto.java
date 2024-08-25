package com.favoriteplace.app.dto.travel;

import lombok.*;

public class PilgrimageSocketDto {
    @Data
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ButtonState {
        private Boolean certifyButtonEnabled;
        private Boolean guestbookButtonEnabled;
        private Boolean multiGuestbookButtonEnabled;
    }
}
